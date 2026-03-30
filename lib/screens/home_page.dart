import 'package:flutter/material.dart';
import '../models/message.dart';
import '../models/chat_session.dart';
import '../data/database_service.dart';
import '../data/openrouter_service.dart';
import '../widgets/settings_dialog.dart';
import '../widgets/message_bubble.dart';
import '../widgets/typing_indicator.dart';

class MyHomePage extends StatefulWidget {
  const MyHomePage({
    super.key,
    required this.title,
    required this.onThemeChanged,
    required this.currentThemeMode,
  });

  final String title;
  final Function(ThemeMode) onThemeChanged;
  final ThemeMode currentThemeMode;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  ChatSession? _currentSession;
  List<ChatSession> _sessions = [];

  final TextEditingController _controller = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  late OpenRouterService _openRouter;
  String? _apiKey;
  String? _selectedModel;
  List<Map<String, dynamic>> _availableModels = [];
  bool _isLoading = false;
  bool _isLoadingModels = false;

  @override
  void initState() {
    super.initState();
    _openRouter = OpenRouterService();
    _loadPreferences();
    _loadSessions();
  }

  Future<void> _loadSessions() async {
    _sessions = await DatabaseService.getSessions();
    final currentId = await DatabaseService.getCurrentSessionId();
    
    if (currentId != null) {
      final existing = _sessions.where((s) => s.id == currentId).firstOrNull;
      if (existing != null) {
        setState(() {
          _currentSession = existing;
        });
        return;
      }
    }
    
    _createNewSession();
  }

  void _createNewSession() {
    final now = DateTime.now();
    final newSession = ChatSession(
      id: now.millisecondsSinceEpoch.toString(),
      title: 'New Chat',
      messages: [],
      createdAt: now,
      updatedAt: now,
    );
    _sessions.insert(0, newSession);
    _currentSession = newSession;
    DatabaseService.setCurrentSessionId(newSession.id);
    DatabaseService.saveSession(_currentSession!);
    setState(() {});
  }

  Future<void> _switchSession(ChatSession session) async {
    await DatabaseService.setCurrentSessionId(session.id);
    setState(() {
      _currentSession = session;
    });
  }

  Future<void> _deleteSession(ChatSession session) async {
    await DatabaseService.deleteSession(session.id);
    _sessions.removeWhere((s) => s.id == session.id);
    
    if (_currentSession?.id == session.id) {
      if (_sessions.isNotEmpty) {
        await _switchSession(_sessions.first);
      } else {
        _createNewSession();
      }
    }
    setState(() {});
  }

  Future<void> _updateCurrentSession() async {
    if (_currentSession == null) return;
    final index = _sessions.indexWhere((s) => s.id == _currentSession!.id);
    if (index != -1) {
      _sessions[index] = _currentSession!;
      await DatabaseService.saveSession(_currentSession!);
    }
  }

  void _loadPreferences() async {
    _apiKey = await DatabaseService.getApiKey();
    _selectedModel = await DatabaseService.getSelectedModel() ?? 'openai/gpt-3.5-turbo'; // Default model
    if (_apiKey != null) {
      await _fetchModels();
    }
    setState(() {});
  }

  Future<void> _fetchModels() async {
    if (_apiKey == null) return;
    setState(() {
      _isLoadingModels = true;
    });
    try {
      _availableModels = await _openRouter.getModels(_apiKey!);
      _availableModels.sort((a, b) => (a['name'] as String).compareTo(b['name'] as String));
      setState(() {
        _isLoadingModels = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingModels = false;
      });
      // Handle error, maybe show snackbar
    }
  }

  void _sendMessage() async {
    if (_controller.text.trim().isEmpty || _isLoading || _currentSession == null) return;

    final userMessage = Message(
      text: _controller.text.trim(),
      isUser: true,
      timestamp: DateTime.now(),
    );

    if (_currentSession!.messages.isEmpty) {
      _currentSession = _currentSession!.copyWith(
        title: _controller.text.trim().length > 30 
          ? '${_controller.text.trim().substring(0, 30)}...' 
          : _controller.text.trim(),
      );
    }

    final messages = List<Message>.from(_currentSession!.messages)..add(userMessage);
    _currentSession = _currentSession!.copyWith(
      messages: messages,
      updatedAt: DateTime.now(),
    );

    setState(() {
      _isLoading = true;
    });

    _controller.clear();

    await _updateCurrentSession();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    });

    if (_apiKey != null) {
      final apiMessages = _currentSession!.messages.map((m) => {
        'role': m.isUser ? 'user' : 'assistant',
        'content': m.text,
      }).toList();

      try {
        final reply = await _openRouter.getCompletion(_apiKey!, apiMessages, model: _selectedModel ?? 'openai/gpt-3.5-turbo');
        if (!mounted) return;
        final replyMessage = Message(
          text: reply,
          isUser: false,
          timestamp: DateTime.now(),
        );
        final updatedMessages = List<Message>.from(_currentSession!.messages)..add(replyMessage);
        _currentSession = _currentSession!.copyWith(
          messages: updatedMessages,
          updatedAt: DateTime.now(),
        );
        await _updateCurrentSession();
        setState(() {
          _isLoading = false;
        });
        WidgetsBinding.instance.addPostFrameCallback((_) {
          _scrollController.animateTo(
            _scrollController.position.maxScrollExtent,
            duration: const Duration(milliseconds: 300),
            curve: Curves.easeOut,
          );
        });
      } catch (e) {
        if (!mounted) return;
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to get response: ${e.toString()}'),
            action: SnackBarAction(
              label: 'Retry',
              onPressed: _sendMessage,
            ),
          ),
        );
      }
    } else {
      setState(() {
        _isLoading = false;
      });
      _showApiKeyDialog();
    }
  }

  void _showApiKeyDialog() {
    showDialog(
      context: context,
      builder: (context) => SettingsDialog(
        apiKey: _apiKey,
        selectedModel: _selectedModel,
        availableModels: _availableModels,
        isLoadingModels: _isLoadingModels,
        currentThemeMode: widget.currentThemeMode,
        onApiKeyChanged: (key) async {
          await DatabaseService.setApiKey(key);
          setState(() {
            _apiKey = key;
          });
          await _fetchModels();
          // Reset selected model if not available
          if (_availableModels.isEmpty || !_availableModels.any((m) => m['id'] == _selectedModel)) {
            final newModel = _availableModels.isNotEmpty ? _availableModels[0]['id'] : null;
            if (newModel != null) {
              await DatabaseService.setSelectedModel(newModel);
              setState(() {
                _selectedModel = newModel;
              });
            }
          }
        },
        onModelChanged: (model) async {
          await DatabaseService.setSelectedModel(model);
          setState(() {
            _selectedModel = model;
          });
        },
        onThemeChanged: widget.onThemeChanged,
      ),
    );
  }

  void _showSessionHistory() {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Chat History',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                ),
                TextButton.icon(
                  onPressed: () {
                    Navigator.pop(context);
                    _createNewSession();
                  },
                  icon: const Icon(Icons.add),
                  label: const Text('New Chat'),
                ),
              ],
            ),
            const Divider(),
            Expanded(
              child: _sessions.isEmpty
                  ? const Center(child: Text('No chats yet'))
                  : ListView.builder(
                      itemCount: _sessions.length,
                      itemBuilder: (context, index) {
                        final session = _sessions[index];
                        final isSelected = session.id == _currentSession?.id;
                        return ListTile(
                          selected: isSelected,
                          title: Text(
                            session.title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          subtitle: Text(
                            '${session.messages.length} messages',
                            style: TextStyle(
                              color: Theme.of(context).colorScheme.onSurfaceVariant,
                            ),
                          ),
                          trailing: IconButton(
                            icon: const Icon(Icons.delete_outline),
                            onPressed: () {
                              Navigator.pop(context);
                              _deleteSession(session);
                            },
                          ),
                          onTap: () {
                            Navigator.pop(context);
                            _switchSession(session);
                          },
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final messages = _currentSession?.messages ?? [];

    return Scaffold(
      appBar: AppBar(
        backgroundColor: colorScheme.surfaceContainerHighest,
        title: GestureDetector(
          onTap: _showSessionHistory,
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Flexible(
                child: Text(
                  _currentSession?.title ?? widget.title,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              const SizedBox(width: 4),
              const Icon(Icons.arrow_drop_down, size: 20),
            ],
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.history),
            onPressed: _showSessionHistory,
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: _showApiKeyDialog,
          ),
        ],
      ),
      body: Column(
        children: [
          if (_apiKey == null)
            Container(
              color: colorScheme.errorContainer,
              padding: const EdgeInsets.all(8),
              child: Row(
                children: [
                  Icon(Icons.warning, color: colorScheme.onErrorContainer),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'API key not set. Configure it in settings to start chatting.',
                      style: TextStyle(color: colorScheme.onErrorContainer),
                    ),
                  ),
                  TextButton(
                    onPressed: _showApiKeyDialog,
                    child: Text(
                      'Set Key',
                      style: TextStyle(color: colorScheme.primary),
                    ),
                  ),
                ],
              ),
            ),
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: messages.length + (_isLoading ? 1 : 0),
              itemBuilder: (context, index) {
                if (index < messages.length) {
                  final message = messages[index];
                  return MessageBubble(message: message);
                } else {
                  return const TypingIndicator();
                }
              },
            ),
          ),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: colorScheme.surface,
              border: Border(
                top: BorderSide(color: colorScheme.outline.withValues(alpha: 0.2)),
              ),
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: InputDecoration(
                      hintText: 'Type a message...',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                      ),
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 12,
                      ),
                    ),
                    onSubmitted: (_) => _sendMessage(),
                  ),
                ),
                const SizedBox(width: 8),
                FloatingActionButton(
                  onPressed: _sendMessage,
                  child: const Icon(Icons.send),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    _scrollController.dispose();
    super.dispose();
  }
}
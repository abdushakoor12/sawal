import 'package:flutter/material.dart';
import '../models/message.dart';
import '../data/preferences_service.dart';
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
  final List<Message> _messages = [];

  final TextEditingController _controller = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  late PreferencesService _prefs;
  late OpenRouterService _openRouter;
  String? _apiKey;
  String? _selectedModel;
  List<Map<String, dynamic>> _availableModels = [];
  bool _isLoading = false;
  bool _isLoadingModels = false;

  @override
  void initState() {
    super.initState();
    _prefs = PreferencesService();
    _openRouter = OpenRouterService();
    _loadPreferences();
  }

  void _loadPreferences() async {
    _apiKey = await _prefs.getApiKey();
    _selectedModel = await _prefs.getSelectedModel() ?? 'openai/gpt-3.5-turbo'; // Default model
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
    if (_controller.text.trim().isEmpty || _isLoading) return;

    final userMessage = Message(
      text: _controller.text.trim(),
      isUser: true,
      timestamp: DateTime.now(),
    );

    setState(() {
      _messages.add(userMessage);
      _isLoading = true;
    });

    _controller.clear();

    // Scroll to bottom
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    });

    if (_apiKey != null) {
      // Build messages for API
      final apiMessages = _messages.map((m) => {
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
        setState(() {
          _messages.add(replyMessage);
          _isLoading = false;
        });
        // Scroll to bottom after reply
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
          await _prefs.saveApiKey(key);
          setState(() {
            _apiKey = key;
          });
          await _fetchModels();
          // Reset selected model if not available
          if (_availableModels.isEmpty || !_availableModels.any((m) => m['id'] == _selectedModel)) {
            final newModel = _availableModels.isNotEmpty ? _availableModels[0]['id'] : null;
            if (newModel != null) {
              await _prefs.saveSelectedModel(newModel);
              setState(() {
                _selectedModel = newModel;
              });
            }
          }
        },
        onModelChanged: (model) async {
          await _prefs.saveSelectedModel(model);
          setState(() {
            _selectedModel = model;
          });
        },
        onThemeChanged: widget.onThemeChanged,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        backgroundColor: colorScheme.surfaceContainerHighest,
        title: Text(widget.title),
        actions: [
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
              itemCount: _messages.length + (_isLoading ? 1 : 0),
              itemBuilder: (context, index) {
                if (index < _messages.length) {
                  final message = _messages[index];
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
import 'package:flutter/material.dart';
import 'theme/gruvbox.dart';
import 'models/message.dart';
import 'data/preferences_service.dart';
import 'data/openrouter_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Sawal Chat',
      theme: GruvboxTheme.light(),
      darkTheme: GruvboxTheme.dark(),
      themeMode: ThemeMode.system,
      home: const MyHomePage(title: 'Sawal Chat'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

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
    final apiKeyController = TextEditingController(text: _apiKey ?? '');
    String? selectedModel = _selectedModel;

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) => AlertDialog(
          title: const Text('OpenRouter Settings'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: apiKeyController,
                decoration: const InputDecoration(hintText: 'Enter your API key'),
                obscureText: true,
              ),
              const SizedBox(height: 16),
              if (_availableModels.isNotEmpty)
                InkWell(
                  onTap: () => _showModelSelectionDialog(context, setState, selectedModel, (newModel) {
                    selectedModel = newModel;
                  }),
                  child: InputDecorator(
                    decoration: const InputDecoration(
                      labelText: 'Selected Model',
                      border: OutlineInputBorder(),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: Text(
                            _availableModels.firstWhere(
                              (m) => m['id'] == selectedModel,
                              orElse: () => {'name': 'Select Model'},
                            )['name'] as String,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        const Icon(Icons.arrow_drop_down),
                      ],
                    ),
                  ),
                )
              else if (_apiKey != null && _isLoadingModels)
                const Row(
                  children: [
                    SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                    SizedBox(width: 8),
                    Text('Loading models...'),
                  ],
                )
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () async {
                final key = apiKeyController.text.trim();
                if (key.isNotEmpty && key != _apiKey) {
                  await _prefs.saveApiKey(key);
                  this.setState(() {
                    _apiKey = key;
                  });
                  // Fetch models with new key
                  await _fetchModels();
                  // Reset selected model if not available
                  if (_availableModels.isEmpty || !_availableModels.any((m) => m['id'] == selectedModel)) {
                    selectedModel = _availableModels.isNotEmpty ? _availableModels[0]['id'] : null;
                  }
                }
                if (selectedModel != null && selectedModel != _selectedModel) {
                  await _prefs.saveSelectedModel(selectedModel!);
                  this.setState(() {
                    _selectedModel = selectedModel;
                  });
                }
                Navigator.pop(context);
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }

  void _showModelSelectionDialog(BuildContext parentContext, StateSetter setState, String? currentModel, Function(String) onModelSelected) {
    String searchQuery = '';
    List<Map<String, dynamic>> filteredModels = List.from(_availableModels);

    showDialog(
      context: parentContext,
      builder: (dialogContext) => StatefulBuilder(
        builder: (context, dialogSetState) => AlertDialog(
          title: const Text('Select Model'),
          content: SizedBox(
            width: double.maxFinite,
            height: 400,
            child: Column(
              children: [
                TextField(
                  decoration: const InputDecoration(
                    hintText: 'Search models...',
                    prefixIcon: Icon(Icons.search),
                  ),
                  onChanged: (value) {
                    searchQuery = value.toLowerCase();
                    filteredModels = _availableModels.where((model) {
                      final name = (model['name'] as String).toLowerCase();
                      final id = (model['id'] as String).toLowerCase();
                      return name.contains(searchQuery) || id.contains(searchQuery);
                    }).toList();
                    dialogSetState(() {});
                  },
                ),
                const SizedBox(height: 8),
                Expanded(
                  child: ListView.builder(
                    itemCount: filteredModels.length,
                    itemBuilder: (context, index) {
                      final model = filteredModels[index];
                      final isSelected = model['id'] == currentModel;
                      return ListTile(
                        title: Text(model['name'] as String),
                        subtitle: Text(model['id'] as String),
                        trailing: isSelected ? const Icon(Icons.check) : null,
                        onTap: () {
                          onModelSelected(model['id'] as String);
                          Navigator.pop(dialogContext);
                        },
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(dialogContext),
              child: const Text('Cancel'),
            ),
          ],
        ),
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
                  final isUser = message.isUser;

                  return Align(
                    alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                    child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 4),
                      padding: const EdgeInsets.all(12),
                      constraints: BoxConstraints(
                        maxWidth: MediaQuery.of(context).size.width * 0.7,
                      ),
                      decoration: BoxDecoration(
                        color: isUser
                            ? colorScheme.primary
                            : colorScheme.surfaceContainer,
                        borderRadius: BorderRadius.only(
                          topLeft: const Radius.circular(16),
                          topRight: const Radius.circular(16),
                          bottomLeft: isUser ? const Radius.circular(16) : const Radius.circular(4),
                          bottomRight: isUser ? const Radius.circular(4) : const Radius.circular(16),
                        ),
                      ),
                      child: Text(
                        message.text,
                        style: TextStyle(
                          color: isUser
                              ? colorScheme.onPrimary
                              : colorScheme.onSurface,
                        ),
                      ),
                    ),
                  );
                } else {
                  // Loading indicator
                  return Align(
                    alignment: Alignment.centerLeft,
                    child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 4),
                      padding: const EdgeInsets.all(12),
                      constraints: BoxConstraints(
                        maxWidth: MediaQuery.of(context).size.width * 0.7,
                      ),
                      decoration: BoxDecoration(
                        color: colorScheme.surfaceContainer,
                        borderRadius: BorderRadius.only(
                          topLeft: const Radius.circular(16),
                          topRight: const Radius.circular(16),
                          bottomLeft: const Radius.circular(4),
                          bottomRight: const Radius.circular(16),
                        ),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          SizedBox(
                            width: 16,
                            height: 16,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          ),
                          const SizedBox(width: 8),
                          Text(
                            'Typing...',
                            style: TextStyle(
                              color: colorScheme.onSurface,
                              fontStyle: FontStyle.italic,
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
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

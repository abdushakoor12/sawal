import 'package:flutter/material.dart';

class SettingsDialog extends StatefulWidget {
  final String? apiKey;
  final String? selectedModel;
  final List<Map<String, dynamic>> availableModels;
  final bool isLoadingModels;
  final Function(String) onApiKeyChanged;
  final Function(String) onModelChanged;

  const SettingsDialog({
    super.key,
    this.apiKey,
    this.selectedModel,
    required this.availableModels,
    required this.isLoadingModels,
    required this.onApiKeyChanged,
    required this.onModelChanged,
  });

  @override
  State<SettingsDialog> createState() => _SettingsDialogState();
}

class _SettingsDialogState extends State<SettingsDialog> {
  late TextEditingController _apiKeyController;
  late String? _selectedModel;

  @override
  void initState() {
    super.initState();
    _apiKeyController = TextEditingController(text: widget.apiKey ?? '');
    _selectedModel = widget.selectedModel;
  }

  @override
  void dispose() {
    _apiKeyController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('OpenRouter Settings'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          TextField(
            controller: _apiKeyController,
            decoration: const InputDecoration(hintText: 'Enter your API key'),
            obscureText: true,
          ),
          const SizedBox(height: 16),
          if (widget.availableModels.isNotEmpty)
            InkWell(
              onTap: () => _showModelSelectionDialog(context),
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
                        widget.availableModels.firstWhere(
                          (m) => m['id'] == _selectedModel,
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
          else if (widget.apiKey != null && widget.isLoadingModels)
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
          onPressed: () {
            final key = _apiKeyController.text.trim();
            if (key.isNotEmpty) {
              widget.onApiKeyChanged(key);
            }
            if (_selectedModel != null) {
              widget.onModelChanged(_selectedModel!);
            }
            Navigator.pop(context);
          },
          child: const Text('Save'),
        ),
      ],
    );
  }

  void _showModelSelectionDialog(BuildContext parentContext) {
    String searchQuery = '';
    List<Map<String, dynamic>> filteredModels = List.from(widget.availableModels);

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
                    filteredModels = widget.availableModels.where((model) {
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
                      final isSelected = model['id'] == _selectedModel;
                      return ListTile(
                        title: Text(model['name'] as String),
                        subtitle: Text(model['id'] as String),
                        trailing: isSelected ? const Icon(Icons.check) : null,
                        onTap: () {
                          setState(() {
                            _selectedModel = model['id'] as String;
                          });
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
}
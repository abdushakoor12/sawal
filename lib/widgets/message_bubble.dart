import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/message.dart';

class MessageBubble extends StatefulWidget {
  final Message message;

  const MessageBubble({super.key, required this.message});

  @override
  State<MessageBubble> createState() => _MessageBubbleState();
}

class _MessageBubbleState extends State<MessageBubble> {
  bool _isHovered = false;
  bool _showActions = false;

  void _copyToClipboard() {
    Clipboard.setData(ClipboardData(text: widget.message.text));
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Copied to clipboard'),
        duration: Duration(seconds: 1),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final isUser = widget.message.isUser;

    return MouseRegion(
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() {
        _isHovered = false;
        _showActions = false;
      }),
      child: GestureDetector(
        onTap: () => setState(() => _showActions = !_showActions),
        child: Align(
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
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.message.text,
                  style: TextStyle(
                    color: isUser
                        ? colorScheme.onPrimary
                        : colorScheme.onSurface,
                  ),
                ),
                if (_isHovered || _showActions)
                  Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        InkWell(
                          onTap: _copyToClipboard,
                          borderRadius: BorderRadius.circular(4),
                          child: Padding(
                            padding: const EdgeInsets.all(2),
                            child: Icon(
                              Icons.copy,
                              size: 16,
                              color: isUser
                                  ? colorScheme.onPrimary.withValues(alpha: 0.7)
                                  : colorScheme.onSurface.withValues(alpha: 0.5),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
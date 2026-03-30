import 'dart:convert';
import 'package:http/http.dart' as http;

class OpenRouterService {
  static const String baseUrl = 'https://openrouter.ai/api/v1';

  Future<String> getCompletion(String apiKey, List<Map<String, dynamic>> messages, {String model = 'openai/gpt-3.5-turbo'}) async {
    final response = await http.post(
      Uri.parse('$baseUrl/chat/completions'),
      headers: {
        'Authorization': 'Bearer $apiKey',
        'Content-Type': 'application/json',
        'HTTP-Referer': '', // Optional, can be left empty
        'X-Title': 'Sawal Chat', // Optional app name
      },
      body: jsonEncode({
        'model': model,
        'messages': messages,
        'max_tokens': 1000, // Limit response length
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['choices'][0]['message']['content'];
    } else {
      throw Exception('Failed to get completion: ${response.statusCode} - ${response.body}');
    }
  }

  Future<List<Map<String, dynamic>>> getModels(String apiKey) async {
    final response = await http.get(
      Uri.parse('$baseUrl/models'),
      headers: {
        'Authorization': 'Bearer $apiKey',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return List<Map<String, dynamic>>.from(data['data']);
    } else {
      throw Exception('Failed to fetch models: ${response.statusCode} - ${response.body}');
    }
  }
}
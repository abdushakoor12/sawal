import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/chat_session.dart';

class PreferencesService {
  static const String _apiKeyKey = 'openrouter_api_key';
  static const String _selectedModelKey = 'selected_model';
  static const String _themeModeKey = 'theme_mode';
  static const String _sessionsKey = 'chat_sessions';
  static const String _currentSessionIdKey = 'current_session_id';

  Future<void> saveApiKey(String apiKey) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_apiKeyKey, apiKey);
  }

  Future<String?> getApiKey() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_apiKeyKey);
  }

  Future<void> saveSelectedModel(String modelId) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_selectedModelKey, modelId);
  }

  Future<String?> getSelectedModel() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_selectedModelKey);
  }

  Future<void> saveThemeMode(String themeMode) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_themeModeKey, themeMode);
  }

  Future<String?> getThemeMode() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_themeModeKey);
  }

  Future<List<ChatSession>> getSessions() async {
    final prefs = await SharedPreferences.getInstance();
    final sessionsJson = prefs.getString(_sessionsKey);
    if (sessionsJson == null) return [];
    
    final List<dynamic> decoded = jsonDecode(sessionsJson);
    return decoded
        .map((s) => ChatSession.fromJson(s as Map<String, dynamic>))
        .toList();
  }

  Future<void> saveSessions(List<ChatSession> sessions) async {
    final prefs = await SharedPreferences.getInstance();
    final encoded = jsonEncode(sessions.map((s) => s.toJson()).toList());
    await prefs.setString(_sessionsKey, encoded);
  }

  Future<String?> getCurrentSessionId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_currentSessionIdKey);
  }

  Future<void> setCurrentSessionId(String? id) async {
    final prefs = await SharedPreferences.getInstance();
    if (id == null) {
      await prefs.remove(_currentSessionIdKey);
    } else {
      await prefs.setString(_currentSessionIdKey, id);
    }
  }
}
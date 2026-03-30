import 'package:shared_preferences/shared_preferences.dart';

class PreferencesService {
  static const String _apiKeyKey = 'openrouter_api_key';
  static const String _selectedModelKey = 'selected_model';
  static const String _themeModeKey = 'theme_mode';

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
}
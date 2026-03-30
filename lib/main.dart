import 'package:flutter/material.dart';
import 'theme/gruvbox.dart';
import 'screens/home_page.dart';
import 'data/preferences_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late PreferencesService _prefs;
  ThemeMode _themeMode = ThemeMode.system;

  @override
  void initState() {
    super.initState();
    _prefs = PreferencesService();
    _loadThemeMode();
  }

  void _loadThemeMode() async {
    final themeModeString = await _prefs.getThemeMode();
    if (themeModeString != null) {
      setState(() {
        _themeMode = ThemeMode.values.firstWhere(
          (mode) => mode.name == themeModeString,
          orElse: () => ThemeMode.system,
        );
      });
    }
  }

  void _changeThemeMode(ThemeMode mode) async {
    await _prefs.saveThemeMode(mode.name);
    setState(() {
      _themeMode = mode;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Sawal Chat',
      theme: GruvboxTheme.light(),
      darkTheme: GruvboxTheme.dark(),
      themeMode: _themeMode,
      home: MyHomePage(
        title: 'Sawal Chat',
        onThemeChanged: _changeThemeMode,
        currentThemeMode: _themeMode,
      ),
    );
  }
}

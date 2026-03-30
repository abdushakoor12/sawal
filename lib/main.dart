import 'package:flutter/material.dart';
import 'theme/gruvbox.dart';
import 'screens/home_page.dart';
import 'data/database_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await DatabaseService.initialize();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ThemeMode _themeMode = ThemeMode.system;

  @override
  void initState() {
    super.initState();
    _loadThemeMode();
  }

  void _loadThemeMode() async {
    final themeModeString = await DatabaseService.getThemeMode();
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
    await DatabaseService.setThemeMode(mode.name);
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

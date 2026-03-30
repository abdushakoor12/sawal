import 'package:flutter/material.dart';
import 'theme/gruvbox.dart';
import 'screens/home_page.dart';

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

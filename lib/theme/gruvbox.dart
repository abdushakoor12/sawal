import 'package:flutter/material.dart';

class GruvboxColors {
  GruvboxColors._();

  static const Color darkBg = Color(0xFF282828);
  static const Color darkBg1 = Color(0xFF3C3836);
  static const Color darkBg2 = Color(0xFF504945);
  static const Color darkBg3 = Color(0xFF665C54);
  static const Color darkBg4 = Color(0xFF7C6F64);

  static const Color darkFg = Color(0xFFEBDBB2);
  static const Color darkFg1 = Color(0xFFD5C4A1);
  static const Color darkFg2 = Color(0xFFBDAE93);
  static const Color darkFg3 = Color(0xFFA89984);
  static const Color darkFg4 = Color(0xFF928374);

  static const Color lightBg = Color(0xFFFBF1C7);
  static const Color lightBg1 = Color(0xFFEBDBB2);
  static const Color lightBg2 = Color(0xFFD5C4A1);
  static const Color lightBg3 = Color(0xFFBDAE93);
  static const Color lightBg4 = Color(0xFFA89984);

  static const Color lightFg = Color(0xFF3C3836);
  static const Color lightFg1 = Color(0xFF504945);
  static const Color lightFg2 = Color(0xFF665C54);
  static const Color lightFg3 = Color(0xFF7C6F64);
  static const Color lightFg4 = Color(0xFF928374);

  static const Color red = Color(0xFFCC241D);
  static const Color redBright = Color(0xFFFB4934);

  static const Color green = Color(0xFF98971A);
  static const Color greenBright = Color(0xFFB8BB26);

  static const Color yellow = Color(0xFFD79921);
  static const Color yellowBright = Color(0xFFFABD2F);

  static const Color blue = Color(0xFF458588);
  static const Color blueBright = Color(0xFF83A598);

  static const Color purple = Color(0xFFB16286);
  static const Color purpleBright = Color(0xFFD3869B);

  static const Color aqua = Color(0xFF689D6A);
  static const Color aquaBright = Color(0xFF8EC07C);

  static const Color orange = Color(0xFFD65D0E);
  static const Color orangeBright = Color(0xFFFE8019);
}

class GruvboxTheme {
  GruvboxTheme._();

  static ThemeData dark() {
    final c = ColorScheme(
      brightness: Brightness.dark,
      primary: GruvboxColors.orangeBright,
      onPrimary: GruvboxColors.darkBg,
      primaryContainer: GruvboxColors.orange,
      onPrimaryContainer: GruvboxColors.darkFg,

      secondary: GruvboxColors.aquaBright,
      onSecondary: GruvboxColors.darkBg,
      secondaryContainer: GruvboxColors.aqua,
      onSecondaryContainer: GruvboxColors.darkFg,

      tertiary: GruvboxColors.purpleBright,
      onTertiary: GruvboxColors.darkBg,
      tertiaryContainer: GruvboxColors.purple,
      onTertiaryContainer: GruvboxColors.darkFg,

      error: GruvboxColors.redBright,
      onError: GruvboxColors.darkBg,
      errorContainer: GruvboxColors.red,
      onErrorContainer: GruvboxColors.darkFg,

      surface: GruvboxColors.darkBg,
      onSurface: GruvboxColors.darkFg,
      surfaceContainerHighest: GruvboxColors.darkBg1,
      surfaceContainerHigh: GruvboxColors.darkBg1,
      surfaceContainer: GruvboxColors.darkBg2,
      surfaceContainerLow: GruvboxColors.darkBg3,
      surfaceContainerLowest: GruvboxColors.darkBg4,

      outline: GruvboxColors.darkBg3,
      outlineVariant: GruvboxColors.darkBg4,

      inverseSurface: GruvboxColors.lightBg,
      onInverseSurface: GruvboxColors.lightFg,
      inversePrimary: GruvboxColors.orange,

      shadow: GruvboxColors.darkBg,
      scrim: GruvboxColors.darkBg,
    );

    return ThemeData(
      colorScheme: c,
      scaffoldBackgroundColor: GruvboxColors.darkBg,
      appBarTheme: AppBarTheme(
        backgroundColor: GruvboxColors.darkBg1,
        foregroundColor: GruvboxColors.darkFg,
        elevation: 0,
        scrolledUnderElevation: 4,
      ),
      cardTheme: CardThemeData(
        color: GruvboxColors.darkBg1,
        elevation: 2,
      ),
      dividerTheme: DividerThemeData(
        color: GruvboxColors.darkBg2,
        thickness: 1,
      ),
      floatingActionButtonTheme: FloatingActionButtonThemeData(
        backgroundColor: GruvboxColors.orange,
        foregroundColor: GruvboxColors.darkBg,
      ),
      textTheme: TextTheme(
        displayLarge: TextStyle(color: GruvboxColors.darkFg),
        displayMedium: TextStyle(color: GruvboxColors.darkFg),
        displaySmall: TextStyle(color: GruvboxColors.darkFg),
        headlineLarge: TextStyle(color: GruvboxColors.darkFg),
        headlineMedium: TextStyle(color: GruvboxColors.darkFg),
        headlineSmall: TextStyle(color: GruvboxColors.darkFg),
        titleLarge: TextStyle(color: GruvboxColors.darkFg),
        titleMedium: TextStyle(color: GruvboxColors.darkFg),
        titleSmall: TextStyle(color: GruvboxColors.darkFg),
        bodyLarge: TextStyle(color: GruvboxColors.darkFg),
        bodyMedium: TextStyle(color: GruvboxColors.darkFg1),
        bodySmall: TextStyle(color: GruvboxColors.darkFg2),
        labelLarge: TextStyle(color: GruvboxColors.darkFg),
        labelMedium: TextStyle(color: GruvboxColors.darkFg1),
        labelSmall: TextStyle(color: GruvboxColors.darkFg2),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: GruvboxColors.darkBg1,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.darkBg2),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.darkBg2),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.orange),
        ),
        hintStyle: TextStyle(color: GruvboxColors.darkFg3),
      ),
      iconTheme: IconThemeData(color: GruvboxColors.darkFg1),
      snackBarTheme: SnackBarThemeData(
        backgroundColor: GruvboxColors.darkBg1,
        contentTextStyle: TextStyle(color: GruvboxColors.darkFg),
      ),
    );
  }

  static ThemeData light() {
    final c = ColorScheme(
      brightness: Brightness.light,
      primary: GruvboxColors.orange,
      onPrimary: GruvboxColors.lightBg,
      primaryContainer: GruvboxColors.orangeBright,
      onPrimaryContainer: GruvboxColors.lightFg,

      secondary: GruvboxColors.aqua,
      onSecondary: GruvboxColors.lightBg,
      secondaryContainer: GruvboxColors.aquaBright,
      onSecondaryContainer: GruvboxColors.lightFg,

      tertiary: GruvboxColors.purple,
      onTertiary: GruvboxColors.lightBg,
      tertiaryContainer: GruvboxColors.purpleBright,
      onTertiaryContainer: GruvboxColors.lightFg,

      error: GruvboxColors.red,
      onError: GruvboxColors.lightBg,
      errorContainer: GruvboxColors.redBright,
      onErrorContainer: GruvboxColors.lightFg,

      surface: GruvboxColors.lightBg,
      onSurface: GruvboxColors.lightFg,
      surfaceContainerHighest: GruvboxColors.lightBg1,
      surfaceContainerHigh: GruvboxColors.lightBg1,
      surfaceContainer: GruvboxColors.lightBg2,
      surfaceContainerLow: GruvboxColors.lightBg3,
      surfaceContainerLowest: GruvboxColors.lightBg4,

      outline: GruvboxColors.lightBg3,
      outlineVariant: GruvboxColors.lightBg4,

      inverseSurface: GruvboxColors.darkBg,
      onInverseSurface: GruvboxColors.darkFg,
      inversePrimary: GruvboxColors.orangeBright,

      shadow: GruvboxColors.lightBg,
      scrim: GruvboxColors.lightBg,
    );

    return ThemeData(
      colorScheme: c,
      scaffoldBackgroundColor: GruvboxColors.lightBg,
      appBarTheme: AppBarTheme(
        backgroundColor: GruvboxColors.lightBg1,
        foregroundColor: GruvboxColors.lightFg,
        elevation: 0,
        scrolledUnderElevation: 4,
      ),
      cardTheme: CardThemeData(
        color: GruvboxColors.lightBg1,
        elevation: 2,
      ),
      dividerTheme: DividerThemeData(
        color: GruvboxColors.lightBg2,
        thickness: 1,
      ),
      floatingActionButtonTheme: FloatingActionButtonThemeData(
        backgroundColor: GruvboxColors.orange,
        foregroundColor: GruvboxColors.lightBg,
      ),
      textTheme: TextTheme(
        displayLarge: TextStyle(color: GruvboxColors.lightFg),
        displayMedium: TextStyle(color: GruvboxColors.lightFg),
        displaySmall: TextStyle(color: GruvboxColors.lightFg),
        headlineLarge: TextStyle(color: GruvboxColors.lightFg),
        headlineMedium: TextStyle(color: GruvboxColors.lightFg),
        headlineSmall: TextStyle(color: GruvboxColors.lightFg),
        titleLarge: TextStyle(color: GruvboxColors.lightFg),
        titleMedium: TextStyle(color: GruvboxColors.lightFg),
        titleSmall: TextStyle(color: GruvboxColors.lightFg),
        bodyLarge: TextStyle(color: GruvboxColors.lightFg),
        bodyMedium: TextStyle(color: GruvboxColors.lightFg1),
        bodySmall: TextStyle(color: GruvboxColors.lightFg2),
        labelLarge: TextStyle(color: GruvboxColors.lightFg),
        labelMedium: TextStyle(color: GruvboxColors.lightFg1),
        labelSmall: TextStyle(color: GruvboxColors.lightFg2),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: GruvboxColors.lightBg1,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.lightBg3),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.lightBg3),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: GruvboxColors.orange),
        ),
        hintStyle: TextStyle(color: GruvboxColors.lightFg3),
      ),
      iconTheme: IconThemeData(color: GruvboxColors.lightFg1),
      snackBarTheme: SnackBarThemeData(
        backgroundColor: GruvboxColors.lightBg1,
        contentTextStyle: TextStyle(color: GruvboxColors.lightFg),
      ),
    );
  }
}

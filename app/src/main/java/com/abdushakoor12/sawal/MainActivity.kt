package com.abdushakoor12.sawal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.ThemeMode
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.ui.screens.home.HomeScreen
import com.abdushakoor12.sawal.ui.theme.SawalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val prefManager = rememberLookup<PrefManager>()
            val selectedThemeMode by prefManager.themeModeFlow.collectAsState(initial = ThemeMode.System)

            SawalTheme(
                darkTheme = when (selectedThemeMode) {
                    ThemeMode.System -> isSystemInDarkTheme()
                    ThemeMode.Light -> false
                    ThemeMode.Dark -> true
                },
            ) {
                Navigator(HomeScreen()) {
                    FadeTransition(it)
                }
            }
        }
    }
}

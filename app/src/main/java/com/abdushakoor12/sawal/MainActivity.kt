package com.abdushakoor12.sawal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.ui.screens.home.HomeScreen
import com.abdushakoor12.sawal.ui.theme.SawalTheme
import com.abdushakoor12.sawal.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val prefManager = rememberLookup<PrefManager>()
            val selectedThemeMode by prefManager.themeModeFlow.collectAsState(initial = ThemeMode.System)
            val isDynamicColor by prefManager.isDynamicColorFlow.collectAsState(initial = true)

            SawalTheme(
                themeMode = selectedThemeMode,
                dynamicColor = isDynamicColor
            ) {
                Navigator(HomeScreen()) {
                    FadeTransition(it)
                }
            }
        }
    }
}

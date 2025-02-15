package com.abdushakoor12.sawal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.abdushakoor12.sawal.ui.screens.home.HomeScreen
import com.abdushakoor12.sawal.ui.theme.SawalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SawalTheme {
                Navigator(HomeScreen()) {
                    FadeTransition(it)
                }
            }
        }
    }
}

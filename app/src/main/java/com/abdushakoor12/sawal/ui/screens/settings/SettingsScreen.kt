package com.abdushakoor12.sawal.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.ThemeMode
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.ui.icons.Clear_day
import com.abdushakoor12.sawal.ui.icons.Mode_night
import com.abdushakoor12.sawal.ui.icons.Night_sight_auto

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val prefManager = rememberLookup<PrefManager>()
        val selectedThemeMode by prefManager.themeModeFlow.collectAsState(initial = ThemeMode.System)

        var themeMenuExpanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    actions = {
                        Box {
                            IconButton(
                                onClick = {
                                    themeMenuExpanded = !themeMenuExpanded
                                }
                            ) {
                                Icon(
                                    when (selectedThemeMode) {
                                        ThemeMode.System -> Night_sight_auto
                                        ThemeMode.Light -> Clear_day
                                        ThemeMode.Dark -> Mode_night
                                    },
                                    contentDescription = selectedThemeMode.name
                                )
                            }

                            DropdownMenu(
                                expanded = themeMenuExpanded,
                                onDismissRequest = { themeMenuExpanded = false }
                            ) {
                                ThemeMode.entries.forEach { themeMode ->
                                    val selected = themeMode == selectedThemeMode
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                when (themeMode) {
                                                    ThemeMode.System -> Night_sight_auto
                                                    ThemeMode.Light -> Clear_day
                                                    ThemeMode.Dark -> Mode_night
                                                },
                                                contentDescription = themeMode.name
                                            )
                                        },
                                        text = { Text(themeMode.name) },
                                        onClick = {
                                            prefManager.saveThemeMode(themeMode)
                                            themeMenuExpanded = false
                                        },
                                        trailingIcon = {
                                            if (selected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },

            ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

            }
        }
    }
}
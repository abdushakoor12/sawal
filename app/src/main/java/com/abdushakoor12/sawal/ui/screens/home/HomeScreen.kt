package com.abdushakoor12.sawal.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.ui.screens.chat.HomeViewModel
import com.abdushakoor12.sawal.ui.screens.home.tabs.CharactersTab
import com.abdushakoor12.sawal.ui.screens.home.tabs.HistoryTab
import com.abdushakoor12.sawal.ui.screens.home.tabs.HomeTab
import com.abdushakoor12.sawal.ui.screens.home.tabs.SettingsTab
import com.abdushakoor12.sawal.ui.screens.select_model.SelectModelScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

        val selectedTab = rememberSaveable { mutableStateOf(HomeTab.Home) }

        val selectedModelId by viewModel.currentModelId.collectAsState()
        val selectedModel by viewModel.currentModel.collectAsState(null)

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (selectedTab.value == HomeTab.Home) {
                            TextButton(onClick = {
                                navigator.push(SelectModelScreen())
                            }) {
                                Text(
                                    selectedModel?.name ?: selectedModelId,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar {
                    HomeTab.entries.forEach { tab ->
                        val selected = selectedTab.value == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    selectedTab.value = tab
                                }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = when (tab) {
                                    HomeTab.Home -> Icons.Default.Home
                                    HomeTab.Characters -> Icons.Default.Person
                                    HomeTab.History -> Icons.AutoMirrored.Filled.List
                                    HomeTab.Settings -> Icons.Default.Settings
                                },
                                contentDescription = tab.name
                            )

                            AnimatedVisibility(
                                visible = selected,
                            ) {
                                Text(
                                    text = tab.name,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                }

            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab.value) {
                    HomeTab.Home -> {
                        HomeTab()
                    }

                    HomeTab.Characters -> {
                        CharactersTab()
                    }

                    HomeTab.History -> {
                        HistoryTab()
                    }

                    HomeTab.Settings -> {
                        SettingsTab()
                    }
                }
            }
        }
    }
}

enum class HomeTab {
    Home, Characters, History, Settings
}
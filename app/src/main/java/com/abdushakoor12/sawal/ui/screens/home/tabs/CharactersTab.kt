package com.abdushakoor12.sawal.ui.screens.home.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.database.AppDatabase
import com.abdushakoor12.sawal.ui.components.AddCharacterDialog
import com.abdushakoor12.sawal.ui.components.CharacterItem
import com.abdushakoor12.sawal.ui.screens.chat.ChatScreen

@Composable
fun CharactersTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val viewModel: CharactersViewModel = viewModel(
        factory = CharactersViewModelFactory(database)
    )

    val navigator = LocalNavigator.currentOrThrow

    val characters by viewModel.characters.collectAsState()
    val isAddingCharacter by viewModel.isAddingCharacter.collectAsState()
    val newCharacterName by viewModel.newCharacterName.collectAsState()
    val newCharacterDescription by viewModel.newCharacterDescription.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (characters.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Characters Yet",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Add characters to personalize your conversations",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            // List of characters
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(characters) { character ->
                    CharacterItem(
                        character = character,
                        onDelete = viewModel::deleteCharacter,
                        onClick = { selectedCharacter ->
                            navigator.push(ChatScreen(character = selectedCharacter))
                        }
                    )
                }
            }
        }

        // Add character FAB
        FloatingActionButton(
            onClick = viewModel::showAddCharacterDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Character"
            )
        }

        // Add character dialog
        if (isAddingCharacter) {
            AddCharacterDialog(
                name = newCharacterName,
                onNameChange = viewModel::onNewCharacterNameChange,
                description = newCharacterDescription,
                onDescriptionChange = viewModel::onNewCharacterDescriptionChange,
                onDismiss = viewModel::hideAddCharacterDialog,
                onConfirm = viewModel::addCharacter
            )
        }
    }
}
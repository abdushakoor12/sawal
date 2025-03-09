package com.abdushakoor12.sawal.ui.screens.chat

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.database.CharacterEntity
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.ui.screens.select_model.SelectModelScreen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

@OptIn(ExperimentalMaterial3Api::class)
@Parcelize
data class ChatScreen(
    var initialMessage: String? = null,
    var chatId: String? = null,
    var character: CharacterEntity? = null,
) : Screen, Parcelable {
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

        LaunchedEffect(key1 = true) {
            initialMessage?.let {
                viewModel.updateMsg(it)
                viewModel.onSendMessage()

                initialMessage = null
            }

            chatId?.let { id ->
                viewModel.changeChatId(id)
            }

            character?.let { char ->
                viewModel.setCharacter(char)
            }
        }

        val availableModels by viewModel.availableModels.collectAsState()
        val msg by viewModel.msg.collectAsState()
        val loading by viewModel.loading.collectAsState()
        val messages by viewModel.messages.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val selectedModelId by viewModel.currentModelId.collectAsState()
        val selectedModel by viewModel.currentModel.collectAsState(null)

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    HomeDrawerSheet(
                        onChatSelected = {
                            viewModel.changeChatId(it.uuid)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            TextButton(onClick = {
                                navigator.push(SelectModelScreen())
                            }) {
                                Text(
                                    selectedModel?.name ?: selectedModelId,
                                    fontSize = 12.sp,
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "History"
                                )
                            }
                            
                            IconButton(onClick = {
                                viewModel.createNewChat()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Chat"
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                HomeScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    messages = messages,
                    loading = loading,
                    msg = msg,
                    onChangeMsg = { viewModel.updateMsg(it) },
                    onSendMessage = { viewModel.onSendMessage() },
                    onToggleFav = { viewModel.toggleFav(it) },
                    character = character
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    msg: String,
    loading: Boolean,
    onChangeMsg: (String) -> Unit,
    messages: List<ChatMessageEntity>,
    onSendMessage: () -> Unit,
    onToggleFav: (ChatMessageEntity) -> Unit,
    character: CharacterEntity? = null
) {
    val prefManager = rememberLookup<PrefManager>()

    val isKeySet by prefManager.isKeySetFlow.collectAsState(initial = false)

    if (!isKeySet) {
        Dialog(onDismissRequest = { }) {

            var apiKey by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Please enter your OpenRouter API key",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("OpenRouter API Key") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        prefManager.setOpenRouterApiKey(apiKey)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save")
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (character != null) {
            var isExpanded by remember { mutableStateOf(false) }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Character Icon",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (character.description.isNotBlank()) {
                            IconButton(onClick = { isExpanded = !isExpanded }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                                )
                            }
                        }
                    }
                    
                    AnimatedVisibility(visible = isExpanded && character.description.isNotBlank()) {
                        Text(
                            text = character.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            if (loading) {
                item(key = "Loading Message") {
                    ChatMessageView(
                        ChatMessageEntity(
                            message = "",
                            role = "assistant",
                            chatId = "",
                        ),
                        dummy = true,
                        onToggleFav = onToggleFav
                    )
                }
            }

            items(messages) { message ->
                ChatMessageView(message, onToggleFav = onToggleFav)
            }
        }

        if (messages.isEmpty()) {
            Text(
                "How can I help you?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = msg,
                onValueChange = { onChangeMsg(it) },
                label = { Text("Your Message...") },
                modifier = Modifier.weight(1f),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSendMessage()
                    }
                )
            )

            AnimatedVisibility(
                visible = msg.isNotBlank(),
            ) {
                FloatingActionButton(onClick = { onSendMessage() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}
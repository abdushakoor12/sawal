package com.abdushakoor12.sawal.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.AIModel
import com.abdushakoor12.sawal.App
import com.abdushakoor12.sawal.R
import com.abdushakoor12.sawal.database.ChatEntity
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = {
                            navigator.push(SettingsScreen())
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            HomeScreenContent(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier) {

    var msg by remember { mutableStateOf("") }

    var availableModels by remember { mutableStateOf(listOf<AIModel>()) }

    var loading by remember { mutableStateOf(false) }

    var chatEntity by remember { mutableStateOf<ChatEntity?>(null) }

    var messages by remember { mutableStateOf(listOf<ChatMessageEntity>()) }

    val context = LocalContext.current

    var selectedModel by remember { mutableStateOf(App.of(context).prefManager.selectedModel()) }

    val repo = App.of(context).repo
    val database = App.of(context).appDatabase

    val scope = rememberCoroutineScope()

    val isKeySet by App.of(context).prefManager.isKeySetFlow.collectAsState(initial = false)

    var showModelChooser by remember { mutableStateOf(false) }

    LaunchedEffect(chatEntity?.uuid) {
        if (chatEntity != null) {
            database.chatMessageEntityDao().getAllMessagesFlow(chatEntity!!.uuid)
                .collect {
                    messages = it
                }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            availableModels = withContext(Dispatchers.IO) {
                repo.getAvailableModels().data
            }
        }
    }

    if (showModelChooser && availableModels.isNotEmpty()) {
        Dialog(onDismissRequest = { showModelChooser = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Please select a model",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(availableModels) { model ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedModel = model.id
                                    App.of(context).prefManager.setSelectedModel(model.id)
                                    showModelChooser = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                model.name,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(3.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSendMessage() {
        val newMessage = msg.trim()
        if (newMessage.isBlank()) return

        val chat = chatEntity ?: ChatEntity(
            title = newMessage,
        ).also { chat ->
            chatEntity = chat
            scope.launch(Dispatchers.IO) {
                database.chatEntityDao().insert(chat)
            }
        }

        scope.launch(Dispatchers.IO) {
            database.chatMessageEntityDao().insert(
                ChatMessageEntity(
                    chatId = chat.uuid,
                    message = newMessage,
                    role = "user"
                )
            )
        }

        msg = ""

        loading = true
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repo.sendMessage(
                        model = selectedModel,
                        message = newMessage,
                    )
                }
                result.choices.firstOrNull()?.message?.let {
                    withContext(Dispatchers.IO) {
                        if (chatEntity == null) {
                            return@withContext
                        }

                        database.chatMessageEntityDao()
                            .insert(
                                ChatMessageEntity(
                                    role = it.role, message = it.content,
                                    chatId = chatEntity!!.uuid,
                                )
                            )

                    }
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "onSendMessage: ${e.message}", e)
                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
            } finally {
                loading = false
            }
        }
    }

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
                        App.of(context).prefManager.setOpenRouterApiKey(apiKey)
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
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedModel,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        showModelChooser = true
                    }
                    .padding(4.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(3.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageView(message)
            }
        }

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                onValueChange = { msg = it },
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
package com.abdushakoor12.sawal

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.abdushakoor12.sawal.database.ChatEntity
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.ui.theme.SawalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SawalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var msg by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    var chatEntity by remember { mutableStateOf<ChatEntity?>(null) }

    var messages by remember { mutableStateOf(listOf<ChatMessageEntity>()) }

    val context = LocalContext.current

    val repo = App.of(context).repo
    val database = App.of(context).appDatabase

    val scope = rememberCoroutineScope()

    val isKeySet by App.of(context).prefManager.isKeySetFlow.collectAsState(initial = false)

    LaunchedEffect(chatEntity?.uuid) {
        if (chatEntity != null) {
            database.chatMessageEntityDao().getAllMessagesFlow(chatEntity!!.uuid)
                .collect {
                    messages = it
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
                    repo.sendMessage(message = newMessage)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                val isUserMessage = message.role == "user"
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        message.message,
                        modifier = Modifier
                            .background(
                                if (isUserMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                            .padding(3.dp),
                        color = if (isUserMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                        textAlign = if (message.role == "user") TextAlign.End else TextAlign.Start
                    )
                }
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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onSendMessage() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}


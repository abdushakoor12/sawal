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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.core.AIRepo
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.data.usecases.GetAvailableORModelsUseCase
import com.abdushakoor12.sawal.database.AppDatabase
import com.abdushakoor12.sawal.database.ChatEntity
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.database.OpenRouterModelEntity
import com.abdushakoor12.sawal.ui.screens.settings.SettingsScreen
import com.abdushakoor12.sawal.ui.theme.SawalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

        val navigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        var chatEntity by remember { mutableStateOf<ChatEntity?>(null) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    HomeDrawerSheet(
                        onChatSelected = {
                            chatEntity = it
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
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
                    modifier = Modifier.padding(innerPadding),
                    chatEntity = chatEntity,
                    onUpdateChatEntity = { chatEntity = it }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    chatEntity: ChatEntity? = null,
    onUpdateChatEntity: (ChatEntity) -> Unit,
) {

    val getAvailableORModelsUseCase = rememberLookup<GetAvailableORModelsUseCase>()

    var msg by remember { mutableStateOf("") }

    var availableModels by remember { mutableStateOf(listOf<OpenRouterModelEntity>()) }

    var loading by remember { mutableStateOf(false) }


    var messages by remember { mutableStateOf(listOf<ChatMessageEntity>()) }

    val prefManager = rememberLookup<PrefManager>()

    val selectedModel by prefManager.selectedModelFlow.collectAsState(initial = "google/gemini-2.0-flash-lite-preview-02-05:free")

    val repo = rememberLookup<AIRepo>()
    val database = rememberLookup<AppDatabase>()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val isKeySet by prefManager.isKeySetFlow.collectAsState(initial = false)

    var showModelChooser by remember { mutableStateOf(false) }

    LaunchedEffect(chatEntity?.uuid) {
        if (chatEntity != null) {
            database.chatMessageEntityDao().getAllMessagesFlow(chatEntity.uuid)
                .collect {
                    messages = it
                }
        }
    }

    LaunchedEffect(Unit) {
        getAvailableORModelsUseCase.invoke().collect {
            availableModels = it
        }
    }

    if (showModelChooser && availableModels.isNotEmpty()) {
        ModelChooseDialog(
            availableModels = availableModels,
            onChange = { prefManager.setSelectedModel(it.id) },
            onClose = { showModelChooser = false }
        )
    }

    fun onSendMessage() {
        val newMessage = msg.trim()
        if (newMessage.isBlank()) return

        val chat = chatEntity ?: ChatEntity(
            title = newMessage,
        ).also { chat ->
            onUpdateChatEntity(chat)
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
                                    chatId = chatEntity.uuid,
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

@Composable
private fun ModelChooseDialog(
    availableModels: List<OpenRouterModelEntity>,
    onChange: (OpenRouterModelEntity) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
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

                    var expanded by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                onChange(model)
                                onClose()
                            }
                            .padding(8.dp),
                    ) {
                        val pricesList = listOf(
                            Pair(model.pricePerPrompt, "prompt"),
                            Pair(model.pricePerCompletion, "completion"),
                            Pair(model.pricePerImage, "image"),
                            Pair(model.pricePerRequest, "request"),
                        ).filter { it.first != "0" }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),

                            ) {
                            Text(
                                model.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )

                            if (pricesList.isNotEmpty()) {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            pricesList.forEach { (price, type) ->
                                Text(
                                    "${price}/$type",
                                    lineHeight = 8.sp,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ModelChooseDialogPreview() {
    SawalTheme {
        ModelChooseDialog(
            availableModels = listOf(
                OpenRouterModelEntity(
                    id = "123",
                    name = "Model 1",
                    pricePerPrompt = "100",
                    pricePerCompletion = "200",
                    pricePerImage = "300",
                    pricePerRequest = "400",
                ),
                OpenRouterModelEntity(
                    id = "456",
                    name = "Model 2",
                    pricePerPrompt = "500",
                    pricePerCompletion = "600",
                    pricePerImage = "700",
                    pricePerRequest = "800",
                ),
            ),
            onChange = {},
            onClose = {}
        )
    }
}
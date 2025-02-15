package com.abdushakoor12.sawal.ui.screens.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.abdushakoor12.sawal.core.AIRepo
import com.abdushakoor12.sawal.core.App
import com.abdushakoor12.sawal.data.usecases.GetAvailableORModelsUseCase
import com.abdushakoor12.sawal.database.ChatEntity
import com.abdushakoor12.sawal.database.ChatEntityDao
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.database.ChatMessageEntityDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val chatEntityDao: ChatEntityDao,
    private val chatMessageEntityDao: ChatMessageEntityDao,
    private val repo: AIRepo,
    getAvailableORModelsUseCase: GetAvailableORModelsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val currentChatId =
        savedStateHandle.getStateFlow("chatId", UUID.randomUUID().toString())
    val msg = savedStateHandle.getStateFlow("msg", "")

    val messages = currentChatId.flatMapLatest { chatId ->
        chatMessageEntityDao.getAllMessagesFlow(chatId)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    val availableModels = getAvailableORModelsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun updateMsg(newValue: String) {
        savedStateHandle["msg"] = newValue
    }

    fun changeChatId(chatId: String) {
        savedStateHandle["chatId"] = chatId
    }

    // TODO: pick model from this viewmodel too
    fun onSendMessage(selectedModel: String) {
        val message = msg.value.trim()
        val chatId = currentChatId.value
        if (message.isBlank() || chatId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            chatEntityDao.insert(
                ChatEntity(
                    title = message,
                    uuid = chatId,
                )
            )

            chatMessageEntityDao.insert(
                ChatMessageEntity(
                    chatId = chatId,
                    message = message,
                    role = "user"
                )
            )

            _loading.update { true }

            try {
                val result = withContext(Dispatchers.IO) {
                    repo.sendMessage(
                        model = selectedModel,
                        message = message,
                    )
                }
                result.choices.firstOrNull()?.message?.let {
                    withContext(Dispatchers.IO) {
                        chatMessageEntityDao
                            .insert(
                                ChatMessageEntity(
                                    role = it.role, message = it.content,
                                    chatId = chatId,
                                )
                            )

                    }
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "onSendMessage: ${e.message}", e)
                // TODO: handle sending toast messages
//                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
            } finally {
                _loading.update { false }
                updateMsg("")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val locator = (this[APPLICATION_KEY] as App).serviceLocator
                HomeViewModel(
                    locator.get(),
                    locator.get(),
                    locator.get(),
                    locator.get(),
                    savedStateHandle
                )
            }
        }
    }
}
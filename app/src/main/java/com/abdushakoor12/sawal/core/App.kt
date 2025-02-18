package com.abdushakoor12.sawal.core

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.abdushakoor12.sawal.data.network.ApiClient
import com.abdushakoor12.sawal.data.network.MessageModel
import com.abdushakoor12.sawal.data.network.MessageRequest
import com.abdushakoor12.sawal.data.responses.ModelsResponse
import com.abdushakoor12.sawal.data.usecases.GetAvailableORModelsUseCase
import com.abdushakoor12.sawal.database.AppDatabase

class App : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()

        val api = ApiClient()

        val prefManager = PrefManager(applicationContext)
        val repo = AIRepo(api, prefManager)

        val appDatabase = AppDatabase.getInstance(applicationContext)
        serviceLocator = ServiceLocator.builder()
            .addSingleton(prefManager)
            .addSingleton(api)
            .addSingleton(repo)
            .addSingleton(appDatabase)
            .addSingleton(appDatabase.chatEntityDao())
            .addSingleton(appDatabase.chatMessageEntityDao())
            .addSingleton(GetAvailableORModelsUseCase(repo, appDatabase.orModelEntityDao()))
            .build()
    }
}

class AIRepo(
    private val api: ApiClient,
    private val prefManager: PrefManager
) {
    suspend fun sendMessage(
        model: String = "google/gemini-2.0-flash-lite-preview-02-05:free",
        messages: List<MessageModel>,
    ) = api.getCompletions(
        "Bearer ${prefManager.getOpenRouterApiKey()}",
        MessageRequest(
            model = model,
            messages = messages
        )
    )

    suspend fun getAvailableModels(): Result<ModelsResponse> =
        Result.runCatching {
            api.getAvailableModels()
        }
}

@Composable
inline fun <reified T : Any> rememberLookup(): T {
    val context = LocalContext.current
    return remember { context.lookup<T>() }
}

inline fun <reified T : Any> Context.lookup(): T {
    return (applicationContext as App).serviceLocator.get<T>()
}
package com.abdushakoor12.sawal.core

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.abdushakoor12.sawal.data.usecases.GetAvailableORModelsUseCase
import com.abdushakoor12.sawal.database.AppDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

class App : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()

        val api = RetrofitInstance.getInstance().create(OpenAIApi::class.java)

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
    private val api: OpenAIApi,
    private val prefManager: PrefManager
) {
    suspend fun sendMessage(
        model: String = "google/gemini-2.0-flash-lite-preview-02-05:free",
        message: String,
    ) = api.getCompletions(
        "Bearer ${prefManager.getOpenRouterApiKey()}",
        mapOf(
            "model" to model, "messages" to listOf(
                mapOf("role" to "user", "content" to message)
            )
        )
    )

    suspend fun getAvailableModels(): Result<ModelsResponse> =
        Result.runCatching {
            api.getAvailableModels()
        }
}

object RetrofitInstance {
    fun getInstance(): Retrofit {
        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val originalRequest = chain.request()
//                chain.proceed(chain.request())
//            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

interface OpenAIApi {
    @POST("chat/completions")
    @JvmSuppressWildcards
    suspend fun getCompletions(
        @Header("Authorization") token: String,
        @Body data: Map<String, Any>
    ): AIResponse

    @GET("models")
    suspend fun getAvailableModels(): ModelsResponse
}

data class ModelsResponse(
    val data: List<AIModel>
)

data class AIModel(
    val id: String,
    val name: String,
    val created: Long,
    val description: String,
    val context_length: Int,
    val architecture: Architecture,
    val pricing: Pricing,
)

data class Architecture(
    val modality: String,
    val tokenizer: String,
    val instruct_type: String?
)

data class Pricing(
    val prompt: String,
    val completion: String,
    val image: String,
    val request: String
)

data class AIResponse(
    val id: String,
    val provider: String,
    val model: String,
    val `object`: String,
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val logprobs: Any?, // Use Any? since it's null in the example
    val finish_reason: String,
    val native_finish_reason: String,
    val index: Int,
    val message: Message
)

data class Message(
    val role: String,
    val content: String,
    val refusal: Any? // Use Any? since it's null in the example
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Composable
inline fun <reified T : Any> rememberLookup(): T {
    val context = LocalContext.current
    return remember { context.lookup<T>() }
}

inline fun <reified T : Any> Context.lookup(): T {
    return (applicationContext as App).serviceLocator.get<T>()
}
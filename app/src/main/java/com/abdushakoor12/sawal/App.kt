package com.abdushakoor12.sawal

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.abdushakoor12.sawal.database.AppDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class App : Application() {

    val prefManager by lazy { PrefManager(applicationContext) }

    val appDatabase by lazy { AppDatabase.getInstance(applicationContext) }

    lateinit var repo: AIRepo
        private set

    override fun onCreate() {
        super.onCreate()

        val api = RetrofitInstance.getInstance().create(OpenAIApi::class.java)

        repo = AIRepo(api, prefManager)
    }

    companion object {
        fun of(context: Context): App {
            return context.applicationContext as App
        }
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
}

class PrefManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isKeySetFlow: Flow<Boolean> = callbackFlow {
        val apiKey = getOpenRouterApiKey()
        trySend(apiKey != null)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "openrouter_api_key") {
                trySend(getOpenRouterApiKey() != null)
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun getOpenRouterApiKey(): String? {
        return sharedPreferences.getString("openrouter_api_key", null)
    }

    @SuppressLint("ApplySharedPref")
    fun setOpenRouterApiKey(apiKey: String) {
        sharedPreferences.edit().putString("openrouter_api_key", apiKey).commit()
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
}

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
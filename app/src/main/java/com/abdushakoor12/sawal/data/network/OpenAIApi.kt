package com.abdushakoor12.sawal.data.network

import com.abdushakoor12.sawal.data.responses.CompletionResponse
import com.abdushakoor12.sawal.data.responses.ModelsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClient {
    suspend fun getCompletions(
        token: String,
        request: MessageRequest,
    ): CompletionResponse {
        return httpClient.post("chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", token)
            setBody(request)
        }.body<CompletionResponse>()
    }

    suspend fun getAvailableModels(): ModelsResponse {
        return httpClient.get("models").body<ModelsResponse>()
    }
}

val httpClient = HttpClient(Android) {
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000L
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    defaultRequest {
        url("https://openrouter.ai/api/v1/")
        header("HTTP-Referer", "https://github.com/abdushakoor12/sawal")
        header("X-Title", "Sawal")
    }
}

@Serializable
data class MessageRequest(
    val model: String,
    val messages: List<MessageModel>
)

@Serializable
data class MessageModel(
    val role: String,
    val content: String,
)
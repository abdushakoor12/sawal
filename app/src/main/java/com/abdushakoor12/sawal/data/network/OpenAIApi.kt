package com.abdushakoor12.sawal.data.network

import com.abdushakoor12.sawal.data.responses.CompletionResponse
import com.abdushakoor12.sawal.data.responses.ModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIApi {
    @POST("chat/completions")
    @JvmSuppressWildcards
    suspend fun getCompletions(
        @Header("Authorization") token: String,
        @Body data: Map<String, Any>
    ): CompletionResponse

    @GET("models")
    suspend fun getAvailableModels(): ModelsResponse
}
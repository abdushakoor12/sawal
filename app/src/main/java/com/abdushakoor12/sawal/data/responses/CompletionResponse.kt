package com.abdushakoor12.sawal.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class CompletionResponse(
    val id: String,
    val provider: String,
    val model: String,
    val `object`: String,
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage
)

@Serializable
data class Choice(
//    val logprobs: Any?, // Use Any? since it's null in the example
    val finish_reason: String,
    val native_finish_reason: String,
    val index: Int,
    val message: Message
)

@Serializable
data class Message(
    val role: String,
    val content: String,
//    val refusal: Any? // Use Any? since it's null in the example
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
package com.abdushakoor12.sawal.data.responses

data class CompletionResponse(
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
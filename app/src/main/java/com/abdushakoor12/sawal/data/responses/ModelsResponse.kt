package com.abdushakoor12.sawal.data.responses

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
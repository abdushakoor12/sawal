package com.abdushakoor12.sawal.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ModelsResponse(
    val data: List<AIModel>
)

@Serializable
data class AIModel(
    val id: String,
    val name: String,
    val created: Long,
    val description: String,
    val context_length: Int,
    val architecture: Architecture,
    val pricing: Pricing,
)

@Serializable
data class Architecture(
    val modality: String,
    val tokenizer: String,
    val instruct_type: String?
)


@Serializable
data class Pricing(
    val prompt: String,
    val completion: String,
    val image: String,
    val request: String
)
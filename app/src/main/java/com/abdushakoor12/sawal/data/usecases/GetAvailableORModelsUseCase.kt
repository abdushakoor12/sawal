package com.abdushakoor12.sawal.data.usecases

import com.abdushakoor12.sawal.core.AIRepo
import com.abdushakoor12.sawal.database.OpenRouterModelEntity
import com.abdushakoor12.sawal.database.OrModelEntityDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Returns a list of available OpenRouter models
 */
class GetAvailableORModelsUseCase(
    private val aiRepo: AIRepo,
    private val orModelDao: OrModelEntityDao,
) {
    operator fun invoke(): Flow<List<OpenRouterModelEntity>> = flow {
        val savedModels = orModelDao.getAllModels()
        emit(savedModels)

        val availableModels = aiRepo.getAvailableModels().getOrNull()?.data ?: emptyList()
        val newModels: List<OpenRouterModelEntity> = availableModels.map { model ->
            OpenRouterModelEntity(
                id = model.id,
                name = model.name,
                pricePerPrompt = model.pricing.prompt,
                pricePerCompletion = model.pricing.completion,
                pricePerImage = model.pricing.image,
                pricePerRequest = model.pricing.request,
            )
        }

        orModelDao.insertAll(newModels)
        emit(newModels)
    }
}
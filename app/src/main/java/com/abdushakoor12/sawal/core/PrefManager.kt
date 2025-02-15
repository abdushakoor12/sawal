package com.abdushakoor12.sawal.core

import android.annotation.SuppressLint
import android.content.Context
import com.abdushakoor12.sawal.utils.ext.stringFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SuppressLint("ApplySharedPref")
class PrefManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isKeySetFlow: Flow<Boolean> =
        sharedPreferences.stringFlow("openrouter_api_key").map { !it.isNullOrBlank() }

    fun getOpenRouterApiKey(): String? {
        return sharedPreferences.getString("openrouter_api_key", null)
    }

    fun selectedModel(): String {
        val defaultModel = "google/gemini-2.0-flash-lite-preview-02-05:free"
        return sharedPreferences.getString("selected_model", defaultModel) ?: defaultModel
    }

    fun setSelectedModel(model: String) {
        sharedPreferences.edit().putString("selected_model", model).commit()
    }

    fun setOpenRouterApiKey(apiKey: String) {
        sharedPreferences.edit().putString("openrouter_api_key", apiKey).commit()
    }
}
package com.abdushakoor12.sawal.core

import android.annotation.SuppressLint
import android.content.Context
import com.abdushakoor12.sawal.ui.theme.ThemeMode
import com.abdushakoor12.sawal.utils.ext.booleanFlow
import com.abdushakoor12.sawal.utils.ext.stringFlow
import com.abdushakoor12.sawal.utils.ext.stringNotNullFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SuppressLint("ApplySharedPref")
class PrefManager(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isKeySetFlow: Flow<Boolean> =
        sharedPreferences.stringFlow("openrouter_api_key").map { !it.isNullOrBlank() }

    val themeModeFlow: Flow<ThemeMode> =
        sharedPreferences.stringFlow("theme_mode").map {
            ThemeMode.entries.find { themeMode -> themeMode.name == it }
                ?: ThemeMode.System
        }

    val isDynamicColorFlow: Flow<Boolean> = sharedPreferences.booleanFlow("dynamic_color", true)

    fun getOpenRouterApiKey(): String? {
        return sharedPreferences.getString("openrouter_api_key", null)
    }

    val selectedModelFlow: Flow<String> = sharedPreferences.stringNotNullFlow(
        "selected_model",
        "google/gemini-2.0-flash-lite-preview-02-05:free"
    )

    fun setSelectedModel(model: String) {
        sharedPreferences.edit().putString("selected_model", model).commit()
    }

    fun setOpenRouterApiKey(apiKey: String) {
        sharedPreferences.edit().putString("openrouter_api_key", apiKey).commit()
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        sharedPreferences.edit().putString("theme_mode", themeMode.name).commit()
    }

    fun setDynamicColorEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean("dynamic_color", value).commit()
    }
}
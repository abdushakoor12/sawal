package com.abdushakoor12.sawal.utils.ext

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

fun SharedPreferences.stringFlow(key: String): Flow<String?> = callbackFlow {
    fun sendValue() = trySend(getString(key, null))

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) {
            sendValue()
        }
    }
    sendValue() // initial value

    registerOnSharedPreferenceChangeListener(listener)
    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}

fun SharedPreferences.stringNotNullFlow(key: String, defaultValue: String): Flow<String> =
    stringFlow(key).map { it ?: defaultValue }
package com.abdushakoor12.sawal.ui.screens.home.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdushakoor12.sawal.database.AppDatabase

class CharactersViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharactersViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
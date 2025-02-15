package com.abdushakoor12.sawal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.abdushakoor12.sawal.core.App
import com.abdushakoor12.sawal.data.usecases.GetAvailableORModelsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    getAvailableORModelsUseCase: GetAvailableORModelsUseCase,
) : ViewModel() {

    val availableModels = getAvailableORModelsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val locator = (this[APPLICATION_KEY] as App).serviceLocator
                HomeViewModel(
                    locator.get(),
                )
            }
        }
    }
}
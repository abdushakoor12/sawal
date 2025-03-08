package com.abdushakoor12.sawal.ui.screens.home.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdushakoor12.sawal.database.AppDatabase
import com.abdushakoor12.sawal.database.CharacterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharactersViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val characterDao = database.characterEntityDao()

    val characters = characterDao.getAllCharactersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAddingCharacter = MutableStateFlow(false)
    val isAddingCharacter: StateFlow<Boolean> = _isAddingCharacter

    private val _newCharacterName = MutableStateFlow("")
    val newCharacterName: StateFlow<String> = _newCharacterName

    private val _newCharacterDescription = MutableStateFlow("")
    val newCharacterDescription: StateFlow<String> = _newCharacterDescription

    fun onNewCharacterNameChange(name: String) {
        _newCharacterName.value = name
    }

    fun onNewCharacterDescriptionChange(description: String) {
        _newCharacterDescription.value = description
    }

    fun showAddCharacterDialog() {
        _isAddingCharacter.value = true
    }

    fun hideAddCharacterDialog() {
        _isAddingCharacter.value = false
        _newCharacterName.value = ""
        _newCharacterDescription.value = ""
    }

    fun addCharacter() {
        if (_newCharacterName.value.isBlank()) return

        viewModelScope.launch {
            val character = CharacterEntity(
                name = _newCharacterName.value,
                description = _newCharacterDescription.value
            )
            characterDao.insert(character)
            hideAddCharacterDialog()
        }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            characterDao.delete(character)
        }
    }
} 
package com.example.android_labs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewModel(
    private val service: RickAndMortyApi = RetrofitClient.RickAndMorty
) : ViewModel() {
    private val _characterData = MutableLiveData<List<Character>>()
    val characterData: LiveData<List<Character>> = _characterData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchCharacters(ids: List<Int>) {
        viewModelScope.launch {
            try {
                val characters = ids.map { id ->
                    service.getCharacter(id)
                }
                _characterData.value = characters
            } catch (e: Exception) {
                when (e) {
                    is IOException -> _errorMessage.value =
                        "Ошибка сети. Проверьте подключение к интернету."

                    is HttpException -> _errorMessage.value = "Ошибка сервера: ${e.code()}. ${e.message()}"
                    else -> _errorMessage.value = "Произошла ошибка: ${e.message}"
                }
            }
        }
    }
}
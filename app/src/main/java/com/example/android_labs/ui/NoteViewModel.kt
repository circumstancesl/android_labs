package com.example.android_labs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_labs.data.INoteRepository
import com.example.android_labs.data.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: INoteRepository) : ViewModel() {
    private val _notes = MutableLiveData<List<NoteModel>>()
    val notes: LiveData<List<NoteModel>> = _notes

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadNotes()
        }
    }

    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _notes.postValue(repository.getAllNotes())
            } catch (e: Exception) {

                _notes.postValue( null)
            }
        }
    }

    fun addNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insetNote(note)
            loadNotes()
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
            loadNotes()
        }
    }
}
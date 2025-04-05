package com.example.android_labs.data

interface INoteRepository {
    suspend fun getAllNotes(): List<NoteModel>
    suspend fun insetNote(note: NoteModel)
    suspend fun deleteNote(note: NoteModel)
}
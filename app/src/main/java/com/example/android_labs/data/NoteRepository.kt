package com.example.android_labs.data

import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDAO: NoteDAO) : INoteRepository {
    override suspend fun getAllNotes(): List<NoteModel> =
        noteDAO.getAll().map { it.toDomain() }

    override suspend fun insetNote(note: NoteModel) {
        noteDAO.insertAll(note.toEntity())
    }

    override suspend fun deleteNote(note: NoteModel) =
        noteDAO.delete(note.toEntity())
}
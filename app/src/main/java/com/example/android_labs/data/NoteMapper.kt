package com.example.android_labs.data

fun Note.toDomain(): NoteModel {
    return NoteModel(
        id = this.id,
        title = this.title ?: "Без названия",
        noteBody = this.noteBody
    )
}

fun NoteModel.toEntity(): Note {
    return Note(
        id = this.id,
        title = this.title,
        noteBody = this.noteBody
    )
}
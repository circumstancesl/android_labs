package com.example.android_labs

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.android_labs.data.INoteRepository
import com.example.android_labs.data.NoteDAO
import com.example.android_labs.data.NoteDatabase
import com.example.android_labs.data.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(context, NoteDatabase::class.java, "note_database")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDAO {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDAO): NoteRepository {
        return NoteRepository(noteDao)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNoteRepository(noteDAO: NoteDAO): INoteRepository {
        return NoteRepository(noteDAO)
    }
}
@HiltAndroidApp
class NoteApplication : Application()
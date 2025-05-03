package com.example.android_labs

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageDescription::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDescriptionDao(): ImageDescriptionDao
}
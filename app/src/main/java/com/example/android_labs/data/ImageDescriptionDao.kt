package com.example.android_labs.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.android_labs.data.ImageDescription

@Dao
interface ImageDescriptionDao {
    @Upsert
    suspend fun upsert(description: ImageDescription)

    @Query("SELECT description FROM image_descriptions WHERE mediaId = :mediaId")
    suspend fun getDescription(mediaId: Long): String?

    @Query("SELECT * FROM image_descriptions")
    suspend fun getAllDescriptions(): List<ImageDescription>
}
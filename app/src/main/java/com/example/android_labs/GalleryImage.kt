package com.example.android_labs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_descriptions")
data class ImageDescription(
    @PrimaryKey
    val mediaId: Long,
    val description: String
)
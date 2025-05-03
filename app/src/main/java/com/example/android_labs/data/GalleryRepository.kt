package com.example.android_labs.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ImageDescriptionDao
) {
    suspend fun getImagesWithDescriptions(): List<ImageItem> {
        val dbData = dao.getAllDescriptions().associateBy { it.mediaId }
        return queryDeviceImages().map { item ->
            item.copy(description = dbData[item.mediaId]?.description ?: "")
        }
    }

    private fun queryDeviceImages(): List<ImageItem> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)

        return context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            mutableListOf<ImageItem>().apply {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    add(
                        ImageItem(
                        mediaId = id,
                        uri = ContentUris.withAppendedId(collection, id),
                        description = ""
                    )
                    )
                }
            }
        } ?: emptyList()
    }

    suspend fun updateDescription(mediaId: Long, description: String) {
        dao.upsert(ImageDescription(mediaId, description))
    }
}
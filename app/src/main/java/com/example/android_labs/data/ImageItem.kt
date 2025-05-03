package com.example.android_labs.data

import android.net.Uri

data class ImageItem(
    val mediaId: Long,
    val uri: Uri,
    val description: String = ""
)
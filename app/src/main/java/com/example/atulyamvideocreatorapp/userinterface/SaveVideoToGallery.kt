package com.example.atulyamvideocreatorapp.userinterface

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException

fun saveVideoToGallery(
    context: Context,
    sourceFile: File
): Uri? {
    if (!sourceFile.exists() || sourceFile.length() == 0L) {
        return null
    }

    val values = ContentValues().apply {
        put(
            MediaStore.Video.Media.DISPLAY_NAME,
            "Bhakti_${System.currentTimeMillis()}.mp4"
        )
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(
            MediaStore.Video.Media.RELATIVE_PATH,
            Environment.DIRECTORY_MOVIES + "/BhaktiVideos"
        )
        put(MediaStore.Video.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val collection = MediaStore.Video.Media.getContentUri(
        MediaStore.VOLUME_EXTERNAL_PRIMARY
    )

    val uri = resolver.insert(collection, values) ?: return null

    try {
        resolver.openOutputStream(uri)?.use { output ->
            sourceFile.inputStream().use { input ->
                input.copyTo(output)
            }
        } ?: throw IOException("Could not open output stream")

        val completedValues = ContentValues().apply {
            put(MediaStore.Video.Media.IS_PENDING, 0)
        }
        resolver.update(uri, completedValues, null, null)

        return uri
    } catch (e: Exception) {
        resolver.delete(uri, null, null)
        throw e
    }
}
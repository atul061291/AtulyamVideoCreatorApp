package com.example.atulyamvideocreatorapp.model

import android.net.Uri
import com.example.atulyamvideocreatorapp.speedcontroller.VideoSpeedPreset
import com.example.atulyamvideocreatorapp.util.MovementPreset

data class EditableSlideItem(
    val id: String,
    var imageUri: Uri,
    // Tracks the independent custom animation preset for this specific picture
    val selectedAnimation: MovementPreset = MovementPreset.GentleZoomIn,
    // Tracks the independent speed preset factor for this specific picture

    val selectedSpeed: VideoSpeedPreset = VideoSpeedPreset.NORMAL,
    val caption: String = ""
)
//{
//    companion object {
//        // FACTORY METHOD: Acts exactly like a constructor but handles the Long logic safely
//        fun createFromLong(
//            id: String,
//            imageUri: Uri,
//            selectedAnimation: MovementPreset,
//            durationMs: Long, // 👈 Accepts the Long
//            caption: String
//        ): EditableSlideItem {
//            // Map the Long to the closest matching Enum preset
//            val resolvedPreset = VideoSpeedPreset.entries.find { it.factor == durationMs }
//                ?: VideoSpeedPreset.NORMAL
//
//            return EditableSlideItem(
//                id = id,
//                imageUri = imageUri,
//                selectedAnimation = selectedAnimation,
//                selectedSpeed = resolvedPreset,
//                caption = caption
//            )
//        }
//    }


//}

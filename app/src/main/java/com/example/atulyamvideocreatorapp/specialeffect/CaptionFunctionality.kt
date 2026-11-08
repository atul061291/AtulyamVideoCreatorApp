//package com.example.atulyamvideocreatorapp.specialeffect
//
//import androidx.annotation.OptIn
//import androidx.compose.runtime.Composable
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.effect.BitmapOverlay
//
//@OptIn(UnstableApi::class)
//fun captionFunctionality(){
//val bulletproofOverlay = object : BitmapOverlay() {
//
//    override fun getBitmap(presentationTimeUs: Long): Bitmap {
//        // 1. Get the current frame time relative to this specific slide item
//        val currentTimeMs = presentationTimeUs / 1_000f
//
//        // 💡 Use the dynamic duration of the active slide directly
//        val totalSlideTime = slide.durationSeconds * 1000f
//
//        val frameBitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(frameBitmap)
//
//        // 2. DEFINE PROPORTIONAL PERCENTAGE PHASES 📊
//        // Phase 1 (Entrance): First 20% of the slide time
//        val entranceEndTime = totalSlideTime * 0.20f
//        // Phase 3 (Reverse Exit): Starts at 80% of the slide time (leaves last 20% for exit)
//        val exitStartTime = totalSlideTime * 0.80f
//
//        val currentX: Float
//        val stationaryX = videoWidth / 2f
//        val startX = -textWidth // Fully hidden off the left side boundary
//
//        when {
//            // 🎬 PHASE A: Entering from Left to Center (0% to 20%)
//            currentTimeMs < entranceEndTime -> {
//                val progress = currentTimeMs / entranceEndTime
//                currentX = startX + (progress * (stationaryX - startX))
//            }
//
//            // 🛑 PHASE B: Dwelling / Stationary in Center (20% to 80%)
//            currentTimeMs >= entranceEndTime && currentTimeMs < exitStartTime -> {
//                currentX = stationaryX
//            }
//
//            // 🏃‍♂️ PHASE C: Reversing Out from Center to Left (80% to 100%)
//            else -> {
//                // Calculate progress strictly within the final 20% time window (0.0 to 1.0)
//                val exitDuration = totalSlideTime - exitStartTime
//                val exitProgress = ((currentTimeMs - exitStartTime) / exitDuration).coerceIn(0f, 1f)
//
//                // Move text backwards from center out to the left edge
//                currentX = stationaryX - (exitProgress * (stationaryX - startX))
//            }
//        }
//
//        // 3. Proportional Typewriter progression masking (Finish typing right when it starts exiting)
//        val typewriterProgress = (currentTimeMs / exitStartTime).coerceIn(0f, 1f)
//        val charsToShow = (typewriterProgress * captionText.length).toInt().coerceIn(0, captionText.length)
//        val partialText = captionText.substring(0, charsToShow)
//
//        val currentY = videoHeight * 0.85f // Fixed bottom positioning
//        textPaint.textAlign = Paint.Align.CENTER
//
//        // 4. Draw the dynamic frame canvas text
//        canvas.drawText(partialText, currentX, currentY, textPaint)
//
//        return frameBitmap
//    }
//
//    override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
//        return OverlaySettings.Builder()
//            .setAlphaScale(1f)
//            .build()
//    }
//}

package com.example.atulyamvideocreatorapp.util

import androidx.annotation.OptIn
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.TextureOverlay
import com.example.atulyamvideocreatorapp.specialeffect.AcceleratedZoomTransformation
import com.example.atulyamvideocreatorapp.specialeffect.CameraShakeTransformation
import com.example.atulyamvideocreatorapp.specialeffect.ColorFilterTransformation
import com.example.atulyamvideocreatorapp.specialeffect.CustomAnimation
import com.example.atulyamvideocreatorapp.specialeffect.CustomOverlay
import com.example.atulyamvideocreatorapp.specialeffect.FadeTransformation
import com.example.atulyamvideocreatorapp.specialeffect.FilterType
import com.example.atulyamvideocreatorapp.specialeffect.FumeSmokeTransformation
import com.example.atulyamvideocreatorapp.specialeffect.Media3ColorComposer
import com.example.atulyamvideocreatorapp.specialeffect.Media3GeometryComposer
import com.example.atulyamvideocreatorapp.specialeffect.PanTransformation
import com.example.atulyamvideocreatorapp.specialeffect.RotationTransformation
import com.google.common.collect.ImmutableList.of

sealed class MovementPreset {

    // Abstract function every single index preset subclass must implement to return its pipeline effects
    @OptIn(UnstableApi::class)
    abstract fun getEffects(durationMs: Long): List<Effect>

    // --- POSITION PRESETS DEFINITIONS ---

    object GentleZoomIn : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(
                startScale = 1.0f,
                endScale = 1.35f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(zoomData)))
        }
    }

    object HorizontalRightPan : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val panData = PanTransformation(
                startX = -0.15f,
                endX = 0.15f,
                startY = 0f,
                endY = 0f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(panData)))
        }
    }

    object HorizontalLeftPan : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val panData = PanTransformation(
                startX = 0.15f,
                endX = -0.15f,
                startY = 0f,
                endY = 0f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(panData)))
        }
    }

    object VerticalTopPan : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val panData = PanTransformation(
                startX = 0f,
                endX = 0f,
                startY = 0.1f,
                endY = -0.1f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(panData)))
        }
    }

    object VerticalBottomPan : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val panData = PanTransformation(
                startX = 0f,
                endX = 0f,
                startY = -0.1f,
                endY = 0.1f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(panData)))
        }
    }

    object SubtleZoomOut : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomOutData = AcceleratedZoomTransformation(
                startScale = 1.3f,
                endScale = 1.0f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(zoomOutData)))
        }
    }

//    object PanWithFumeSmokeCombo : MovementPreset() {
//        @OptIn(UnstableApi::class)
//        override fun getEffects(durationMs: Long): List<Effect> {
//            val panData = PanTransformation(
//                startX = -0.1f,
//                endX = 0.1f,
//                startY = 0f,
//                endY = 0f,
//                totalDurationMs = durationMs
//            )
//            val fumeData = FumeSmokeTransformation(totalDurationMs = durationMs)
//
//            return listOf(
//                Media3GeometryComposer(listOf(panData)),
//                androidx.media3.effect.OverlayEffect(of(CustomOverlay(fumeData)) as List<TextureOverlay>)
//            )
//        }
//    }

    object DramaticZoomIn : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val deepZoomData = AcceleratedZoomTransformation(
                startScale = 1.0f,
                endScale = 1.7f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(deepZoomData)))
        }
    }

    object ClockwiseTilt : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val rotateData = RotationTransformation(
                startDegrees = 0f,
                endDegrees = 8f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(rotateData)))
        }
    }
    object SlowAntiClockwiseRotation : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val rotateData = RotationTransformation(
                startDegrees = 0f,
                endDegrees = -8f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(rotateData)))
        }
    }object AntiClockAndClockwiseRotation : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val rotateData = RotationTransformation(
                startDegrees = -4f,
                endDegrees = 4f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(rotateData)))
        }
    }

    object IntenseCameraShake : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val shakeData = CameraShakeTransformation(intensity = 24f, totalDurationMs = durationMs)
            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(shakeData)))
        }
    }
    object ZoomRumbleCombo : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(startScale = 1.0f, endScale = 1.5f, totalDurationMs = durationMs)
            val shakeData = CameraShakeTransformation(intensity = 14f, totalDurationMs = durationMs)

            // Pass both to the geometry composer to run them simultaneously on the GPU!
            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(zoomData, shakeData)))
        }
    }
    object TiltShakeCombo : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val rotateData = RotationTransformation(startDegrees = 0f, endDegrees = 6f, totalDurationMs = durationMs)
            val shakeData = CameraShakeTransformation(intensity = 10f, totalDurationMs = durationMs)

            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(rotateData, shakeData)))
        }
    }
    object SmoothFullSpin360 : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            // 1. Pre-zoom the image and keep it held at 1.45x scale throughout the duration
            // This crops out the outer corners so the background canvas never leaks black borders
            val cropZoomData = AcceleratedZoomTransformation(
                startScale = 1.45f,
                endScale = 1.45f,
                totalDurationMs = durationMs
            )

            // 2. Perform a full 360-degree rotation sweep
            // For counter-clockwise inversion matching your setup, use 360f. For clockwise, use -360f.
            val spinData = RotationTransformation(
                startDegrees = 0f,
                endDegrees = 90f,
                totalDurationMs = durationMs
            )

            // Pass both into the geometry matrix composer to run concurrently on the GPU
            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(cropZoomData, spinData)))
        }
    }
    object SmoothFullAntiSpin360 : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            // 1. Pre-zoom the image and keep it held at 1.45x scale throughout the duration
            // This crops out the outer corners so the background canvas never leaks black borders
            val cropZoomData = AcceleratedZoomTransformation(
                startScale = 1.45f,
                endScale = 1.45f,
                totalDurationMs = durationMs
            )

            // 2. Perform a full 360-degree rotation sweep
            // For counter-clockwise inversion matching your setup, use 360f. For clockwise, use -360f.
            val spinData = RotationTransformation(
                startDegrees = 0f,
                endDegrees = -90f,
                totalDurationMs = durationMs
            )

            // Pass both into the geometry matrix composer to run concurrently on the GPU
            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(cropZoomData, spinData)))
        }
    }

    object SepiaZoomCombo : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(
                startScale = 1.0f,
                endScale = 1.25f,
                totalDurationMs = durationMs
            )
            val sepiaData =
                ColorFilterTransformation(FilterType.SEPIA, totalDurationMs = durationMs)
            return listOf(
                Media3GeometryComposer(listOf(zoomData)),
                Media3ColorComposer(listOf(sepiaData))
            )
        }
    }

    object SoftOpacityFadeIn : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val fadeData =
                FadeTransformation(startAlpha = 0.0f, endAlpha = 1.0f, totalDurationMs = durationMs)
            return listOf(Media3ColorComposer(listOf(fadeData)))
        }
    }

    object DiagonalZoomPanCombo : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(
                startScale = 1.0f,
                endScale = 1.3f,
                totalDurationMs = durationMs
            )
            val panData = PanTransformation(
                startX = 0f,
                endX = 0f,
                startY = -0.1f,
                endY = 0.1f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(zoomData, panData)))
        }
    }

    object BlackAndWhiteMoody : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val bwData =
                ColorFilterTransformation(FilterType.BLACK_AND_WHITE, totalDurationMs = durationMs)
            return listOf(Media3ColorComposer(listOf(bwData)))
        }
    }

    object AntiClockwiseTilt : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val rotateData = RotationTransformation(
                startDegrees = 0f,
                endDegrees = -6f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(rotateData)))
        }
    }

    object ZoomOutHorizontalPan : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(
                startScale = 1.4f,
                endScale = 1.1f,
                totalDurationMs = durationMs
            )
            val panData = PanTransformation(
                startX = 0.1f,
                endX = -0.1f,
                startY = 0f,
                endY = 0f,
                totalDurationMs = durationMs
            )
            return listOf(Media3GeometryComposer(listOf(zoomData,panData)))
        }
    }
    object CinematicSlowGlideCombo : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<androidx.media3.common.Effect> {
            // 1. Slow & Subtle Zoom: Grows gently from 1.15x to 1.35x
            // Starting pre-zoomed at 1.15f safely protects the edges from leaking black borders
            val slowZoomData = AcceleratedZoomTransformation(
                startScale = 1.15f,
                endScale = 1.35f,
                totalDurationMs = durationMs
            )

            // 2. Smooth Diagonal Glide: Drifts slowly from bottom-left to top-right
            val slowDiagonalPan = PanTransformation(
                startX = -0.15f,
                endX = 0.15f,
                startY = -0.08f,
                endY = 0.08f,
                totalDurationMs = durationMs
            )

            // 3. Elegant Micro-Tilt: Rotates gently by just 3 degrees
            // (Uses -3f to counter-balance your environment's inversion and tilt gracefully)
            val subtleTilt = RotationTransformation(
                startDegrees = 0f,
                endDegrees = -3f,
                totalDurationMs = durationMs
            )

            // Combine all 3 spatial shifts into the geometry composer to run fluidly on the GPU
            return listOf(Media3GeometryComposer(listOf<CustomAnimation>(slowZoomData, slowDiagonalPan, subtleTilt)))
        }
    }
    object ElegantDriftAndFadeIn : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            // 1. Hold a constant crop zoom to protect the edges from showing black canvas lines
            val cropZoomData = AcceleratedZoomTransformation(
                startScale = 1.2f,
                endScale = 1.2f,
                totalDurationMs = durationMs
            )

            // 2. Slow Panoramic Glide: Drifts slowly from left to right
            val slowPanData = PanTransformation(
                startX = -0.15f,
                endX = 0.15f,
                startY = 0f,
                endY = 0f,
                totalDurationMs = durationMs
            )

            // 3. Elegant Transparency Reveal: Fades smoothly from invisible (0.0) to fully visible (1.0)
            val smoothFadeIn = FadeTransformation(
                startAlpha = 0.0f,
                endAlpha = 1.0f,
                totalDurationMs = durationMs
            )

            // Return both the geometric motion composer and the color filter composer to run together
            return listOf(
                Media3GeometryComposer(listOf<CustomAnimation>(cropZoomData, slowPanData)),
                Media3ColorComposer(listOf<CustomAnimation>(smoothFadeIn))
            )
        }
    }


    object ActionShakeWithBWFilter : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val shakeData = CameraShakeTransformation(intensity = 15f, totalDurationMs = durationMs)
            val bwData =
                ColorFilterTransformation(FilterType.BLACK_AND_WHITE, totalDurationMs = durationMs)
            return listOf(
                Media3GeometryComposer(listOf(shakeData)),
                Media3ColorComposer(listOf(bwData))
            )
        }
    }

    object ZoomWithFadeOut : MovementPreset() {
        @OptIn(UnstableApi::class)
        override fun getEffects(durationMs: Long): List<Effect> {
            val zoomData = AcceleratedZoomTransformation(
                startScale = 1.0f,
                endScale = 1.35f,
                totalDurationMs = durationMs
            )
            val fadeData =
                FadeTransformation(startAlpha = 1.0f, endAlpha = 0.0f, totalDurationMs = durationMs)
            return listOf(
                Media3GeometryComposer(listOf(zoomData)),
                Media3ColorComposer(listOf(fadeData))
            )
        }
    }

    companion object {
        // A centralized ordered mapping strategy database assigning indexing tracks to presets
        fun fromIndex(index: Int): MovementPreset {
            return when (index % 15) {
                0 -> GentleZoomIn
                1 -> SubtleZoomOut
                2 -> HorizontalRightPan
                3 -> HorizontalLeftPan
                4 -> VerticalTopPan
                5 -> VerticalBottomPan
                6 -> AntiClockwiseTilt
                7 -> ClockwiseTilt
                8 -> AntiClockAndClockwiseRotation
                9 -> CinematicSlowGlideCombo
                10 -> ElegantDriftAndFadeIn
                11 -> GentleZoomIn
                12 -> SubtleZoomOut
                13 -> HorizontalRightPan
                14 -> HorizontalLeftPan
                else -> GentleZoomIn // Structural fallback safety
            }
        }
    }
}

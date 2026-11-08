package com.example.atulyamvideocreatorapp.specialeffect

sealed interface CustomAnimation {
    val totalDurationMs: Long
    data class ButterflyOverlayTransformation(override val totalDurationMs: Long) : CustomAnimation
    data class HeartOverlayTransformation(override val totalDurationMs: Long) : CustomAnimation
    data class FluidFlashTransformation(override val totalDurationMs: Long) : CustomAnimation
}

// Geometric movements
data class AcceleratedZoomTransformation(
    val startScale: Float, val endScale: Float, override val totalDurationMs: Long
) : CustomAnimation

data class PanTransformation(
    val startX: Float, val endX: Float,
    val startY: Float, val endY: Float, override val totalDurationMs: Long
) : CustomAnimation

data class RotationTransformation(
    val startDegrees: Float, val endDegrees: Float, override val totalDurationMs: Long
) : CustomAnimation

data class CameraShakeTransformation(
    val intensity: Float, override val totalDurationMs: Long
) : CustomAnimation

// Color modifications
data class FadeTransformation(
    val startAlpha: Float, val endAlpha: Float, override val totalDurationMs: Long
) : CustomAnimation

enum class FilterType { BLACK_AND_WHITE, SEPIA }
data class ColorFilterTransformation(
    val filterType: FilterType, override val totalDurationMs: Long
) : CustomAnimation

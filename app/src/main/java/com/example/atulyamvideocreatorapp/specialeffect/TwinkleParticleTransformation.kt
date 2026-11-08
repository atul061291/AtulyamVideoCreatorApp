package com.example.atulyamvideocreatorapp.specialeffect


// Add these to your existing file
data class TwinkleParticleTransformation(
    val density: Float = 30.0f,      // Number of stars
    val sparkleSpeed: Float = 5.0f,  // How fast they blink
    override val totalDurationMs: Long
) : CustomAnimation

data class FumeSmokeTransformation(
    val smokeDensity: Float = 0.4f,  // Opacity intensity layer
    val driftSpeed: Float = 1.2f,    // Moving fog wind speed
    override val totalDurationMs: Long
) : CustomAnimation

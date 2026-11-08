    package com.example.atulyamvideocreatorapp.specialeffect

    import android.graphics.Matrix
    import androidx.media3.common.util.UnstableApi
    import androidx.media3.effect.MatrixTransformation
    import kotlin.random.Random

    @UnstableApi
    class Media3GeometryComposer(
        private val animations: List<CustomAnimation>
    ) : MatrixTransformation {

        override fun getMatrix(presentationTimeUs: Long): Matrix {
            val matrix = Matrix()
            val currentMs = presentationTimeUs / 1000f

            for (animation in animations) {
                // FIX: Use modulo (%) to reset the time relative to this image's specific duration loop
                val relativeTimeMs = currentMs % animation.totalDurationMs
                val progress = (relativeTimeMs / animation.totalDurationMs).coerceIn(0f, 1f)

                when (animation) {
                    is AcceleratedZoomTransformation -> {
                        val scale = lerp(animation.startScale, animation.endScale, progress)
                        matrix.postScale(scale, scale, 0f, 0f)
                    }

                    is PanTransformation -> {
                        val x = lerp(animation.startX, animation.endX, progress)
                        val y = lerp(animation.startY, animation.endY, progress)
                        matrix.postTranslate(x, y)
                    }

                    is RotationTransformation -> {
                        val degrees = lerp(animation.startDegrees, animation.endDegrees, progress)
                        matrix.postRotate(degrees, 0f, 0f)
                    }

                    is CameraShakeTransformation -> {
                        if (progress < 1.0f) {
                            val shakeX = (Random.nextFloat() * 2 - 1) * animation.intensity
                            val shakeY = (Random.nextFloat() * 2 - 1) * animation.intensity
                            matrix.postTranslate(shakeX, shakeY)
                        }
                    }
                    else -> {
                }
                }
            }
            return matrix
        }

        private fun lerp(start: Float, stop: Float, fraction: Float): Float =
            start + fraction * (stop - start)
    }

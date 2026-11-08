package com.example.atulyamvideocreatorapp.specialeffect

import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.RgbMatrix

@UnstableApi
class Media3ColorComposer(
    private val animations: List<CustomAnimation>
) : RgbMatrix {

    override fun getMatrix(presentationTimeUs: Long, useHdr: Boolean): FloatArray {
        val colorMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

        val currentMs = presentationTimeUs / 1000f

        for (animation in animations) {
            // FIX: Use modulo (%) to reset the time relative to this image's specific duration loop
            val relativeTimeMs = currentMs % animation.totalDurationMs
            val progress = (relativeTimeMs / animation.totalDurationMs).coerceIn(0f, 1f)

            when (animation) {
                is FadeTransformation -> {
                    val alpha = lerp(animation.startAlpha, animation.endAlpha, progress)
                    colorMatrix[15] = alpha
                }

                is ColorFilterTransformation -> {
                    when (animation.filterType) {
                        FilterType.BLACK_AND_WHITE -> {
                            val r = 0.299f;
                            val g = 0.587f;
                            val b = 0.114f
                            colorMatrix[0] = r; colorMatrix[1] = g; colorMatrix[2] = b
                            colorMatrix[4] = r; colorMatrix[5] = g; colorMatrix[6] = b
                            colorMatrix[8] = r; colorMatrix[9] = g; colorMatrix[10] = b
                        }

                        FilterType.SEPIA -> {
                            colorMatrix[0] = 0.393f; colorMatrix[1] = 0.769f; colorMatrix[2] =
                                0.189f
                            colorMatrix[4] = 0.349f; colorMatrix[5] = 0.686f; colorMatrix[6] =
                                0.168f
                            colorMatrix[8] = 0.272f; colorMatrix[9] = 0.534f; colorMatrix[10] =
                                0.131f
                        }

                    }
                }

                else -> {

                }
            }
        }
        return colorMatrix
    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float =
        start + fraction * (stop - start)
}

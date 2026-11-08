package com.example.atulyamvideocreatorapp.specialeffect

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BitmapOverlay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@UnstableApi
class CustomOverlayOnlyTwinkle(
    private val animation: CustomAnimation
) : BitmapOverlay() {

    // --- PAINTS ---
    private val particlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val butterflyPaint = Paint().apply {
        color = Color.parseColor("#FF69B4") // Vibrant pink/magenta wings
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#4A4A4A") // Soft black butterfly body
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val heartPaint = Paint().apply {
        color = Color.argb(220, 255, 255, 255) // Semi-transparent clean white hearts
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // --- OBJECT TRACKER PROPERTIES ---
    private data class MovingObject(
        val startX: Float, val startY: Float,
        val speedX: Float, val speedY: Float,
        val size: Float,
        val seed: Float
    )

    // Pre-generate a list of 45 elements with smaller, delicate sizes
    private val elementsList = List(45) {
        MovingObject(
            startX = Random.nextFloat(),
            startY = Random.nextFloat(),
            speedX = 0f, // No structural hard linear side-to-side speed needed
            // Pleasant vertical downward speeds (35 to 75 pixels per second)
            speedY = Random.nextFloat() * 40f + 35f,
            // FIX: Reduced sizes (from 2dp to 5dp max) for a delicate, subtle look
            size = Random.nextFloat() * 3f + 2f,
            seed = Random.nextFloat() * 100f
        )
    }


    private val canvasWidth = 1080
    private val canvasHeight = 1080
    private var frameBitmap: Bitmap? = null
    private var renderCanvas: Canvas? = null

    override fun getBitmap(presentationTimeUs: Long): Bitmap {
        if (frameBitmap == null) {
            frameBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
            renderCanvas = Canvas(frameBitmap!!)
        }

        val bitmap = frameBitmap!!
        val canvas = renderCanvas!!

        // Clear old frame drawing paths
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val runningTimeSec = (presentationTimeUs / 1_000_000f)
        val width = canvasWidth.toFloat()
        val height = canvasHeight.toFloat()

        when (animation) {
            is TwinkleParticleTransformation -> {
                // --- DELICATE, PLEASANT TOP-TO-BOTTOM DRIFTING PARTICLES ---
                elementsList.forEachIndexed { index, particle ->

                    // 1. Calculate a soft, gentle side-to-side sway using a slow sine wave
                    // This gives a pleasant drifting floating path instead of dropping straight down
                    val horizontalSway = sin(runningTimeSec * 1.2f + particle.seed) * 15f
                    val x = (particle.startX * width + horizontalSway) % width

                    // 2. Continuous independent downward movement calculation (adds to Y over time)
                    val downwardMoveY = particle.speedY * runningTimeSec
                    val y = (particle.startY * height + downwardMoveY) % height

                    // 3. Relaxed, elegant blinking speed modifier logic (between 2.0 and 4.0)
                    // Slowing the frequency down makes the sparkling effect highly pleasant and less frantic
                    val calmBlinkSpeed = 2.0f + (index % 3)
                    val blink = sin(runningTimeSec * calmBlinkSpeed + particle.seed) * 0.5f + 0.5f

                    // Dynamic alpha fading opacity based on its blinking rhythm
                    particlePaint.alpha = (blink * 230).toInt()
                        .coerceIn(0, 255) // Max out slightly under 255 for a softer look

                    // Keep tracking coordinate bounds safe inside the canvas  matrix layout
                    val finalX = if (x < 0) x + width else x
                    val finalY = if (y < 0) y + height else y

                    // Draw the smaller, beautifully drifting star point element
                    canvas.drawCircle(
                        finalX,
                        finalY,
                        particle.size * (0.5f + blink * 0.5f), // Smooth pulse sizing scale shifts
                        particlePaint
                    )
                }
            }


            is CustomAnimation.ButterflyOverlayTransformation -> {
                // --- 2. MOVING BUTTERFLIES WITH FLAPPING WINGS ---
                elementsList.forEach { butterfly ->
                    // Unique curved flight path across the layout framework
                    val waveY = sin(runningTimeSec * 2.5f + butterfly.seed) * 70f
                    val x =
                        (butterfly.startX * width + (butterfly.speedX + 50f) * runningTimeSec) % (width + 120f) - 60f
                    val y = (butterfly.startY * height + waveY) % height

                    // High-frequency absolute sine wave simulating wing flap cycles
                    val wingFlapScale = abs(sin(runningTimeSec * 14.0f + butterfly.seed))

                    canvas.save()
                    canvas.translate(x, y)

                    // Left Wing shape mapping
                    val leftWing = Path().apply {
                        moveTo(0f, 0f)
                        cubicTo(
                            -butterfly.size * 2.5f * wingFlapScale,
                            -butterfly.size * 2f,
                            -butterfly.size * 3f * wingFlapScale,
                            butterfly.size,
                            0f,
                            0f
                        )
                    }
                    canvas.drawPath(leftWing, butterflyPaint)

                    // Right Wing shape mapping
                    val rightWing = Path().apply {
                        moveTo(0f, 0f)
                        cubicTo(
                            butterfly.size * 2.5f * wingFlapScale,
                            -butterfly.size * 2f,
                            butterfly.size * 3f * wingFlapScale,
                            butterfly.size,
                            0f,
                            0f
                        )
                    }
                    canvas.drawPath(rightWing, butterflyPaint)

                    // Small center body intersection overlay
                    canvas.drawOval(
                        -2f,
                        -butterfly.size * 0.8f,
                        2f,
                        butterfly.size * 0.8f,
                        bodyPaint
                    )
                    canvas.restore()
                }
            }

            is CustomAnimation.HeartOverlayTransformation -> {
                // --- 3. GENTLY RISING WHITE HEARTS ---
                elementsList.forEach { heart ->
                    // Floating upwards trajectory calculation (subtract Y) with a horizontal sway
                    val swayX = sin(runningTimeSec * 2.0f + heart.seed) * 45f
                    val x = (heart.startX * width + swayX) % width
                    val y = (heart.startY * height - (heart.speedY + 40f) * runningTimeSec) % height

                    // Wrap back onto canvas viewport bottom once it leaves the top boundary edge
                    val finalY =
                        if (y < -50f) y + height + 100f else if (y > height + 50f) y - height - 100f else y

                    canvas.save()
                    canvas.translate(x, finalY)

                    val size = heart.size * 1.3f
                    val heartPath = Path().apply {
                        moveTo(0f, -size / 4f)
                        cubicTo(-size / 2f, -size, -size, -size / 3f, 0f, size)
                        cubicTo(size, -size / 3f, size / 2f, -size, 0f, -size / 4f)
                    }
                    canvas.drawPath(heartPath, heartPaint)
                    canvas.restore()
                }
            }

            else -> {}
        }

        return bitmap
    }
}

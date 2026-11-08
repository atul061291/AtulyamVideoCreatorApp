package com.example.atulyamvideocreatorapp.specialeffect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlaySettings
import com.example.atulyamvideocreatorapp.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@UnstableApi
class CustomOverlay(
    private val context: Context,
    private val animation: CustomAnimation,
    private val isFluidActive: Boolean = true,
    private val isHeartsActive: Boolean = true,
    private val isParticlesActive: Boolean = true,
    private val isButterfliesActive: Boolean = true
) : BitmapOverlay() {

    // --- PAINTS ---
    // Change color to an elegant dark silhouette or pure white depending on your template style
    private val forestPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val particlePaint =
        Paint().apply { color = Color.WHITE; style = Paint.Style.FILL; isAntiAlias = true }
    private val whiteWingPaint = Paint().apply {
        color = Color.parseColor("#FAFAFA"); style = Paint.Style.FILL; isAntiAlias = true
    }
    private val creamBodyPaint = Paint().apply {
        color = Color.parseColor("#E6E1DA"); style = Paint.Style.FILL; isAntiAlias = true
    }
    private val antennaPaint = Paint().apply {
        color = Color.parseColor("#7A7570"); style = Paint.Style.STROKE; strokeWidth =
        2.0f; isAntiAlias = true
    }
    private val assetPaint = Paint().apply { isAntiAlias = true; isFilterBitmap = true }

    // --- DRAWABLE CONTAINER ---
    private val heartSrcBitmap: Bitmap? = try {
        BitmapFactory.decodeResource(context.resources, R.drawable.heartwithoubackground)
    } catch (e: Exception) {
        null
    }

    private data class GraphicElement(
        val startX: Float, val startY: Float,
        val speedX: Float, val speedY: Float,
        val baseSize: Float,
        val modifier: Float,
        val seed: Float
    )

    private val particlePool = List(35) {
        GraphicElement(
            Random.nextFloat(),
            Random.nextFloat(),
            0f,
            Random.nextFloat() * 40f + 35f,
            Random.nextFloat() * 5.0f + 3.5f,
            Random.nextFloat() * 0.5f + 0.8f,
            Random.nextFloat() * 100f
        )
    }

    private val butterflyPool = List(8) {
        GraphicElement(
            startX = Random.nextFloat(), startY = Random.nextFloat(),
            speedX = Random.nextFloat() * 50f + 35f, speedY = Random.nextFloat() * 40f + 15f,
            baseSize = Random.nextFloat() * 10f + 22f,
            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f
        )
    }

    private val heartPool = List(12) {
        GraphicElement(
            startX = Random.nextFloat(), startY = Random.nextFloat(),
            speedX = 0f, speedY = Random.nextFloat() * 45f + 35f,
            baseSize = Random.nextFloat() * 12f + 36f,
            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f
        )
    }

    // FIX 1: Set to a standard 9:16 high-definition vertical video dimension boundary
    private val canvasWidth = 1080
    private val canvasHeight = 1920 // Expanded from 1080 to 1920 for full portrait height coverage!
    private var frameBitmap: Bitmap? = null
    private var renderCanvas: Canvas? = null
    private val workingPath = Path()

    // Pre-allocated coordinates to draw organic jagged pine tree/forest spikes
    private val forestSpikeSeeds = List(15) { Random.nextFloat() * 60f + 40f }

    override fun getTextureSize(presentationTimeUs: Long): Size = Size(canvasWidth, canvasHeight)

    override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
        return OverlaySettings.Builder().setScale(1.0f, 1.0f).setBackgroundFrameAnchor(0f, 0f)
            .build()
    }

    override fun getBitmap(presentationTimeUs: Long): Bitmap {
        if (frameBitmap == null) {
            frameBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
            renderCanvas = Canvas(frameBitmap!!)
        }

        val bitmap = frameBitmap!!
        val canvas = renderCanvas!!
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

        val currentMs = presentationTimeUs / 1000f
        val durationMs = animation.totalDurationMs
        val relativeTimeMs = currentMs % durationMs
        val runningTimeSec = (presentationTimeUs / 1_000_000f)

        val width = canvasWidth.toFloat()
        val height = canvasHeight.toFloat()

        // --- LAYER A: ADVANCED FOREST OPENING TRANSITION (FULL SCREEN COUPLING) ---
        if (animation is CustomAnimation.FluidFlashTransformation) {
            val transitionDuration = 700f
            if (isFluidActive && relativeTimeMs < transitionDuration) {
                val progress = relativeTimeMs / transitionDuration

                // Soft fade-out timing curve as the forest curtains clear away
                val alphaFactor = 1.0f - (progress * progress)
                forestPaint.alpha = (alphaFactor * 255).toInt().coerceIn(0, 255)

                val centerLine = width / 2f
                // Pulls back outward from center (0f) to max edge sides as time advances
                val openingWidth = centerLine * progress

                // 1. LEFT FOREST CURTAIN WIPING AWAY OUTWARD TO THE LEFT EDGE
                workingPath.reset()
                workingPath.moveTo(0f, 0f)
                val leftEdgeX = centerLine - openingWidth
                workingPath.lineTo(leftEdgeX, 0f)

                // Draw nested jagged fractal points down the curtain layer edge line mimicking branches/trees
                val segmentHeightLeft = height / forestSpikeSeeds.size
                forestSpikeSeeds.forEachIndexed { i, spikeWidth ->
                    val currentY = i * segmentHeightLeft
                    val dynamicSway = sin(progress * 4f + i) * 15f
                    // Create tree branch spikes sticking inward
                    workingPath.lineTo(
                        leftEdgeX + spikeWidth * (1f - progress) + dynamicSway,
                        currentY + segmentHeightLeft / 2f
                    )
                    workingPath.lineTo(leftEdgeX, currentY + segmentHeightLeft)
                }
                workingPath.lineTo(0f, height)
                workingPath.close()
                canvas.drawPath(workingPath, forestPaint)

                // 2. RIGHT FOREST CURTAIN WIPING AWAY OUTWARD TO THE RIGHT EDGE
                workingPath.reset()
                workingPath.moveTo(width, 0f)
                val rightEdgeX = centerLine + openingWidth
                workingPath.lineTo(rightEdgeX, 0f)

                val segmentHeightRight = height / forestSpikeSeeds.size
                forestSpikeSeeds.asReversed().forEachIndexed { i, spikeWidth ->
                    val currentY = i * segmentHeightRight
                    val dynamicSway = cos(progress * 4f + i) * 15f
                    // Mirror tree branch spikes sticking inward
                    workingPath.lineTo(
                        rightEdgeX - spikeWidth * (1f - progress) + dynamicSway,
                        currentY + segmentHeightRight / 2f
                    )
                    workingPath.lineTo(rightEdgeX, currentY + segmentHeightRight)
                }
                workingPath.lineTo(width, height)
                workingPath.close()
                canvas.drawPath(workingPath, forestPaint)
            }
            return bitmap
        }

        // --- LAYER B: CONTINUOUS ASSETS ---
        // 1. Falling Particles Track
        if (isParticlesActive) {
            particlePool.forEachIndexed { index, star ->
                val horizontalSway = sin(runningTimeSec * 1.2f + star.seed) * 15f
                val x = (star.startX * width + horizontalSway) % width
                val y = (star.startY * height + star.speedY * runningTimeSec) % height
                val calmBlink = sin(runningTimeSec * (2.2f + (index % 3)) + star.seed) * 0.5f + 0.5f
                particlePaint.alpha = (calmBlink * 240).toInt().coerceIn(0, 255)
                val finalX = if (x < 0) x + width else x
                val finalY = if (y < 0) y + height else y
                canvas.drawCircle(
                    finalX,
                    finalY,
                    star.baseSize * (0.4f + calmBlink * 0.6f),
                    particlePaint
                )
            }
        }

        // 2. Swallowtail Butterflies Track
        if (isButterfliesActive) {
            butterflyPool.forEach { butterfly ->
                val waveY = sin(runningTimeSec * (2.2f * butterfly.modifier) + butterfly.seed) * 55f
                val x =
                    (butterfly.startX * width + (butterfly.speedX + 35f) * runningTimeSec) % (width + 250f) - 120f
                val y = (butterfly.startY * height + waveY) % height
                val wingFlap =
                    abs(sin(runningTimeSec * 13.0f * butterfly.modifier + butterfly.seed))

                canvas.save()
                canvas.translate(x, y)
                val size = butterfly.baseSize

                workingPath.reset()
                workingPath.moveTo(0f, -size * 0.2f)
                workingPath.cubicTo(
                    -size * 1.5f * wingFlap,
                    -size * 2.2f,
                    -size * 3.8f * wingFlap,
                    -size * 1.8f,
                    -size * 3.6f * wingFlap,
                    -size * 0.3f
                )
                workingPath.cubicTo(
                    -size * 3.2f * wingFlap,
                    size * 0.4f,
                    -size * 1.8f * wingFlap,
                    size * 0.5f,
                    0f,
                    0f
                )
                workingPath.cubicTo(
                    -size * 1.8f * wingFlap,
                    size * 0.5f,
                    -size * 2.8f * wingFlap,
                    size * 1.1f,
                    -size * 2.5f * wingFlap,
                    size * 1.9f
                )
                workingPath.lineTo(-size * 2.1f * wingFlap, size * 1.7f)
                workingPath.cubicTo(
                    -size * 1.6f * wingFlap,
                    size * 2.3f,
                    -size * 0.8f * wingFlap,
                    size * 1.7f,
                    0f,
                    size * 0.3f
                )
                workingPath.close()
                canvas.drawPath(workingPath, whiteWingPaint)

                workingPath.reset()
                workingPath.moveTo(0f, -size * 0.2f)
                workingPath.cubicTo(
                    size * 1.5f * wingFlap,
                    -size * 2.2f,
                    size * 3.8f * wingFlap,
                    -size * 1.8f,
                    size * 3.6f * wingFlap,
                    -size * 0.3f
                )
                workingPath.cubicTo(
                    size * 3.2f * wingFlap,
                    size * 0.4f,
                    size * 1.8f * wingFlap,
                    size * 0.5f,
                    0f,
                    0f
                )
                workingPath.cubicTo(
                    size * 1.8f * wingFlap,
                    size * 0.5f,
                    size * 2.8f * wingFlap,
                    size * 1.1f,
                    size * 2.5f * wingFlap,
                    size * 1.9f
                )
                workingPath.lineTo(size * 2.1f * wingFlap, size * 1.7f)
                workingPath.cubicTo(
                    size * 1.6f * wingFlap,
                    size * 2.3f,
                    size * 0.8f * wingFlap,
                    size * 1.7f,
                    0f,
                    size * 0.3f
                )
                workingPath.close()
                canvas.drawPath(workingPath, whiteWingPaint)
                canvas.drawOval(-3.0f, -size * 0.8f, 3.0f, size * 0.6f, creamBodyPaint)
                canvas.drawCircle(0f, -size * 0.9f, 4.0f, creamBodyPaint)
                workingPath.reset()
                workingPath.moveTo(-1f, -size * 0.9f)
                workingPath.quadTo(-6f, -size * 1.4f, -9f, -size * 1.8f)
                workingPath.moveTo(1f, -size * 0.9f)
                workingPath.quadTo(6f, -size * 1.4f, 9f, -size * 1.8f)
                canvas.drawPath(workingPath, antennaPaint)
                canvas.restore()
            }
        }
// 3. Floating Image Assets Hearts Track
        if (isHeartsActive && heartSrcBitmap != null) {
            heartPool.forEach { heart ->
                val swayX = sin(runningTimeSec * 2.0f + heart.seed) * 40f
                val x = (heart.startX * width + swayX) % width
                val y = (heart.startY * height - (heart.speedY + 35f) * runningTimeSec) % height
                val finalY =
                    if (y < -100f) y + height + 200f else if (y > height + 100f) y - height - 100f else y
                canvas.save()
                canvas.translate(x, finalY)
                val currentSize = heart.baseSize.toInt()
                val targetRect = android.graphics.Rect(
                    -currentSize / 2,
                    -currentSize / 2,
                    currentSize / 2,
                    currentSize / 2
                )
                canvas.drawBitmap(heartSrcBitmap, null, targetRect, assetPaint)
                canvas.restore()
            }
        }
        return bitmap
    }
}


////package com.example.atulyamvideocreatorapp.specialeffect
////
////import android.graphics.Bitmap
////import android.graphics.Canvas
////import android.graphics.Color
////import android.graphics.Paint
////import android.graphics.Path
////import androidx.media3.common.util.Size
////import androidx.media3.common.util.UnstableApi
////import androidx.media3.effect.BitmapOverlay
////import androidx.media3.effect.OverlaySettings
////import kotlin.math.abs
////import kotlin.math.cos
////import kotlin.math.sin
////import kotlin.random.Random
////
////@UnstableApi
////class CustomOverlay(
////    private val animation: CustomAnimation,
////    private val isFluidActive: Boolean = true,
////    private val isHeartsActive: Boolean = true,
////    private val isParticlesActive: Boolean = true,
////    private val isButterfliesActive: Boolean = true
////) : BitmapOverlay() {
////
////    // --- PAINTS BUNDLE ---
////    private val fluidFlashPaint = Paint().apply {
////        color = Color.WHITE
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val particlePaint = Paint().apply {
////        color = Color.WHITE
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val butterflyPaint = Paint().apply {
////        color = Color.WHITE // Solid white butterfly wings
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val bodyPaint = Paint().apply {
////        color = Color.parseColor("#4A4A4A")
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    // MODIFICATION: Changed heart paint to solid FILL for high visibility, colourful rendering
////    private val heartPaint = Paint().apply {
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    // --- OBJECT TRACKER SCHEMATICS ---
////    private data class GraphicElement(
////        val startX: Float, val startY: Float,
////        val speedX: Float, val speedY: Float,
////        val baseSize: Float,
////        val modifier: Float,
////        val seed: Float,
////        val colorInt: Int
////    )
////
////    // Vibrant palette list for non-transparent colorful hearts
////    private val heartColors = listOf(
////        Color.parseColor("#E63946"), // Ruby Red
////        Color.parseColor("#FF007F"), // Neon Pink
////        Color.parseColor("#00BFFF"), // Sky Blue
////        Color.parseColor("#A855F7"), // Soft Violet Purple
////        Color.parseColor("#FFB703")  // Golden Yellow
////    )
////
////    private val particlePool = List(35) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = 0f, speedY = Random.nextFloat() * 40f + 35f,
////            baseSize = Random.nextFloat() * 4f + 2.5f,
////            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f,
////            colorInt = Color.WHITE
////        )
////    }
////
////    private val butterflyPool = List(8) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = Random.nextFloat() * 50f + 35f, speedY = Random.nextFloat() * 40f + 15f,
////            baseSize = Random.nextFloat() * 15f + 40f,
////            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f,
////            colorInt = Color.WHITE
////        )
////    }
////
////    private val heartPool = List(15) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = 0f, speedY = Random.nextFloat() * 45f + 35f,
////            baseSize = Random.nextFloat() * 6f + 12f,
////            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f,
////            // MODIFICATION: Randomise each heart's colour from the custom palette array
////            colorInt = heartColors[Random.nextInt(heartColors.size)]
////        )
////    }
////
////    private val canvasWidth = 1080
////    private val canvasHeight = 1080
////    private var frameBitmap: Bitmap? = null
////    private var renderCanvas: Canvas? = null
////
////    override fun getTextureSize(presentationTimeUs: Long): Size {
////        return Size(canvasWidth, canvasHeight)
////    }
////
////    override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
////        return OverlaySettings.Builder()
////            .setScale(1.0f, 1.0f)
////            .setBackgroundFrameAnchor(0f, 0f)
////            .build()
////    }
////
////    override fun getBitmap(presentationTimeUs: Long): Bitmap {
////        if (frameBitmap == null) {
////            frameBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
////            renderCanvas = Canvas(frameBitmap!!)
////        }
////
////        val bitmap = frameBitmap!!
////        val canvas = renderCanvas!!
////        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
////
////        val currentMs = presentationTimeUs / 1000f
////        val durationMs = animation.totalDurationMs
////        val relativeTimeMs = currentMs % durationMs
////        val runningTimeSec = (presentationTimeUs / 1_000_000f)
////
////        val width = canvasWidth.toFloat()
////        val height = canvasHeight.toFloat()
////
////        val workingPath = Path()
////
////        // --- LAYER A: ENTRANCE FLUID FLASH (100% HORIZONTAL AND VERTICAL SCREEN WIPES) ---
////        if (animation is CustomAnimation.FluidFlashTransformation) {
////            // FIX: Guard the fluid splash execution using the UI flag parameter
////            if (isFluidActive && relativeTimeMs < 600f) {
////                val progress = relativeTimeMs / 600f
////                val alphaFactor = 1.0f - (progress * progress)
////                fluidFlashPaint.alpha = (alphaFactor * 255).toInt().coerceIn(0, 255)
////                // MODIFICATION: Calculate exact 100% horizontal and vertical coverage sweeps over time progress
////                val horizontalReach = width * progress
////                val verticalReach = height * progress
////
////                // 1. Full-screen top-left outward liquid wave block
////                workingPath.reset()
////                workingPath.moveTo(0f, 0f)
////                workingPath.lineTo(horizontalReach, 0f)
////                workingPath.cubicTo(
////                    horizontalReach + (50f * sin(progress * 5f)), verticalReach * 0.5f,
////                    horizontalReach - (50f * cos(progress * 4f)), verticalReach * 0.8f,
////                    0f, verticalReach
////                )
////                workingPath.lineTo(0f, 0f)
////                workingPath.close()
////                canvas.drawPath(workingPath, fluidFlashPaint)
////
////                // 2. Full-screen bottom-right inward fluid wave block mirror
////                workingPath.reset()
////                workingPath.moveTo(width, height)
////                workingPath.lineTo(width - horizontalReach, height)
////                workingPath.cubicTo(
////                    width - horizontalReach - (50f * sin(progress * 5f)),
////                    height - (verticalReach * 0.5f),
////                    width - horizontalReach + (50f * cos(progress * 4f)),
////                    height - (verticalReach * 0.8f),
////                    width,
////                    height - verticalReach
////                )
////                workingPath.lineTo(width, height)
////                workingPath.close()
////                canvas.drawPath(workingPath, fluidFlashPaint)
////            }
////            return bitmap
////        }
////
////
////        // --- LAYER B: CONTINUOUS ASSETS ---
////        // 1. Falling Particles
////        if (isParticlesActive) {
////            particlePool.forEachIndexed { index, star ->
////                val horizontalSway = sin(runningTimeSec * 1.2f + star.seed) * 15f
////                val x = (star.startX * width + horizontalSway) % width
////                val y = (star.startY * height + star.speedY * runningTimeSec) % height
////
////                val calmBlink =
////                    sin(runningTimeSec * (2.0f + (index % 3)) + star.seed) * 0.5f + 0.5f
////                particlePaint.alpha = (calmBlink * 230).toInt().coerceIn(0, 255)
////
////                val finalX = if (x < 0) x + width else x
////                val finalY = if (y < 0) y + height else y
////                canvas.drawCircle(
////                    finalX,
////                    finalY,
////                    star.baseSize * (0.5f + calmBlink * 0.5f),
////                    particlePaint
////                )
////            }
////        }
////
////        // 2. Majestic White Butterflies (Small wings, thick body, curly feelers)
////        if (isButterfliesActive) {
////            butterflyPool.forEach { butterfly ->
////                val waveY =
////                    sin(runningTimeSec * (2.2f * butterfly.modifier) + butterfly.seed) * 55f
////                val x =
////                    (butterfly.startX * width + (butterfly.speedX + 35f) * runningTimeSec) % (width + 250f) - 120f
////                val y = (butterfly.startY * height + waveY) % height
////
////                val wingFlap =
////                    abs(sin(runningTimeSec * 13.0f * butterfly.modifier + butterfly.seed))
////
////                canvas.save()
////                canvas.translate(x, y)
////
////                val size = butterfly.baseSize
////
////                val leftWing = Path().apply {
////                    moveTo(0f, 0f)
////                    cubicTo(
////                        -size * 2.8f * wingFlap,
////                        -size * 1.5f,
////                        -size * 3.0f * wingFlap,
////                        size * 0.8f,
////                        0f,
////                        0f
////                    )
////                }
////                canvas.drawPath(leftWing, butterflyPaint)
////
////                val rightWing = Path().apply {
////                    moveTo(0f, 0f)
////                    cubicTo(
////                        size * 2.8f * wingFlap,
////                        -size * 1.5f,
////                        size * 3.0f * wingFlap,
////                        size * 0.8f,
////                        0f,
////                        0f
////                    )
////                }
////                canvas.drawPath(rightWing, butterflyPaint)
////
////                canvas.drawOval(-4.5f, -size * 1.4f, 4.5f, size * 1.4f, bodyPaint)
////
////                // Feeler Antennas
////                workingPath.reset()
////                workingPath.moveTo(-1f, -size * 1.4f)
////                workingPath.cubicTo(-12f, -size * 1.9f, -18f, -size * 1.8f, -16f, -size * 2.1f)
////                workingPath.moveTo(1f, -size * 1.4f)
////                workingPath.cubicTo(12f, -size * 1.9f, 18f, -size * 1.8f, 16f, -size * 2.1f)
////
////                val antennaPaint = Paint().apply {
////                    color = Color.parseColor("#4A4A4A"); style =
////                    Paint.Style.STROKE; strokeWidth = 2.5f; isAntiAlias = true
////                }
////                canvas.drawPath(workingPath, antennaPaint)
////
////                canvas.restore()
////            }
////        }
////
////        // 3. Colourful Solid Hearts (MODIFICATION: Not transparent, fully filled vibrant colors)
////        if (isParticlesActive) {
////            heartPool.forEach { heart ->
////                val swayX = sin(runningTimeSec * 2.0f + heart.seed) * 40f
////                val x = (heart.startX * width + swayX) % width
////                val y = (heart.startY * height - (heart.speedY + 35f) * runningTimeSec) % height
////                val finalY =
////                    if (y < -100f) y + height + 200f else if (y > height + 100f) y - height - 100f else y
////
////                canvas.save()
////                canvas.translate(x, finalY)
////// Apply this specific heart instance's randomized palette color
////                heartPaint.color = heart.colorInt
////                val size = heart.baseSize
////                workingPath.reset()
////                workingPath.moveTo(0f, -size / 4f)
////                workingPath.cubicTo(
////                    -size * 2.2f,
////                    -size * 1.3f,
////                    -size * 2.4f,
////                    -size / 3f,
////                    0f,
////                    size * 1.4f
////                )
////                workingPath.cubicTo(
////                    size * 2.4f,
////                    -size / 3f,
////                    size * 2.2f,
////                    -size * 1.3f,
////                    0f,
////                    -size / 4f
////                )
////                workingPath.close()
////                canvas.drawPath(workingPath, heartPaint)
////                canvas.restore()
////            }
////        }
////        return bitmap
////    }
////}
//
//
////package com.example.atulyamvideocreatorapp.specialeffect
////
////import android.graphics.Bitmap
////import android.graphics.Canvas
////import android.graphics.Color
////import android.graphics.Paint
////import android.graphics.Path
////import androidx.media3.common.util.Size
////import androidx.media3.common.util.UnstableApi
////import androidx.media3.effect.BitmapOverlay
////import androidx.media3.effect.OverlaySettings
////import kotlin.math.abs
////import kotlin.math.cos
////import kotlin.math.sin
////import kotlin.random.Random
////
////@UnstableApi
////class CustomOverlay(
////    private val animation: CustomAnimation
////) : BitmapOverlay() {
////
////    // --- PAINTS BUNDLE ---
////    private val fluidFlashPaint = Paint().apply {
////        color = Color.WHITE
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val particlePaint = Paint().apply {
////        color = Color.WHITE
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    // Dynamic paint used for drawing the custom colored butterfly wings
////    private val butterflyPaint = Paint().apply {
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val bodyPaint = Paint().apply {
////        color = Color.parseColor("#3A3A3A")
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    private val heartPaint = Paint().apply {
////        color = Color.WHITE
////        style = Paint.Style.FILL
////        isAntiAlias = true
////    }
////
////    // --- OBJECT TRACKER SCHEMATICS ---
////    private data class GraphicElement(
////        val startX: Float, val startY: Float,
////        val speedX: Float, val speedY: Float,
////        val baseSize: Float,
////        val modifier: Float,
////        val seed: Float,
////        val colorInt: Int // FIX: Added color attribute unique to each butterfly
////    )
////
////    // Vibrant pastel palette list for multi-color butterfly distribution
////    private val butterflyColors = listOf(
////        Color.parseColor("#FF69B4"), // Vibrant Pink
////        Color.parseColor("#4ECDC4"), // Pastel Cyan/Blue
////        Color.parseColor("#9B5DE5"), // Soft Purple
////        Color.parseColor("#F15BB5"), // Deep Magenta
////        Color.parseColor("#FEE440")  // Soft Sun Gold
////    )
////
////    private val particlePool = List(35) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = 0f, speedY = Random.nextFloat() * 40f + 35f,
////            baseSize = Random.nextFloat() * 4f + 2.5f,
////            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f,
////            colorInt = Color.WHITE
////        )
////    }
////
////    private val butterflyPool = List(12) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = Random.nextFloat() * 60f + 40f, speedY = Random.nextFloat() * 50f + 20f,
////            baseSize = Random.nextFloat() * 6f + 9f, // Delicate, elegant base size
////            modifier = Random.nextFloat() * 0.6f + 0.7f, seed = Random.nextFloat() * 100f,
////            // FIX: Assign a completely random color to each butterfly from our library palette
////            colorInt = butterflyColors[Random.nextInt(butterflyColors.size)]
////        )
////    }
////
////    private val heartPool = List(15) {
////        GraphicElement(
////            startX = Random.nextFloat(), startY = Random.nextFloat(),
////            speedX = 0f, speedY = Random.nextFloat() * 50f + 35f,
////            baseSize = Random.nextFloat() * 6f + 7f,
////            modifier = Random.nextFloat() * 0.5f + 0.8f, seed = Random.nextFloat() * 100f,
////            colorInt = Color.WHITE
////        )
////    }
////
////    private val canvasWidth = 1080
////    private val canvasHeight = 1080
////    private var frameBitmap: Bitmap? = null
////    private var renderCanvas: Canvas? = null
////
////    private val fluidAngles = List(18) { Random.nextFloat() * 360f }
////
////    override fun getTextureSize(presentationTimeUs: Long): Size {
////        return Size(canvasWidth, canvasHeight)
////    }
////
////    override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
////        return OverlaySettings.Builder()
////            .setScale(1.0f, 1.0f)
////            .setBackgroundFrameAnchor(0f, 0f)
////            .build()
////    }
////
////    override fun getBitmap(presentationTimeUs: Long): Bitmap {
////        if (frameBitmap == null) {
////            frameBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
////            renderCanvas = Canvas(frameBitmap!!)
////        }
////
////        val bitmap = frameBitmap!!
////        val canvas = renderCanvas!!
////        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
////
////        val currentMs = presentationTimeUs / 1000f
////        val durationMs = animation.totalDurationMs
////        val relativeTimeMs = currentMs % durationMs
////        val runningTimeSec = (presentationTimeUs / 1_000_000f)
////
////        val width = canvasWidth.toFloat()
////        val height = canvasHeight.toFloat()
////
////        // --- LAYER A: ENTRANCE FLUID FLASH ---
////        if (animation is CustomAnimation.FluidFlashTransformation) {
////            val transitionDuration = 600f
////            if (relativeTimeMs < transitionDuration) {
////                val progress = relativeTimeMs / transitionDuration
////                val alphaFactor = 1.0f - (progress * progress)
////                fluidFlashPaint.alpha = (alphaFactor * 255).toInt().coerceIn(0, 255)
////
////                val centerX = width / 2f
////                val centerY = height / 2f
////
////                val fluidPath = Path()
////                val maxExplosionRadius = width * 1.3f
////                val currentRadius = maxExplosionRadius * progress
////
////                fluidAngles.forEachIndexed { i, angleDegrees ->
////                    val angleRad = Math.toRadians(angleDegrees.toDouble())
////                    val x1 = (centerX + cos(angleRad) * currentRadius).toFloat()
////                    val y1 = (centerY + sin(angleRad) * currentRadius).toFloat()
////
////                    val branchRad = Math.toRadians((angleDegrees + 28f).toDouble())
////                    val x2 = (x1 + cos(branchRad) * (currentRadius * 0.35f)).toFloat()
////                    val y2 = (y1 + sin(branchRad) * (currentRadius * 0.35f)).toFloat()
////
////                    if (i == 0) fluidPath.moveTo(centerX, centerY)
////
////                    fluidPath.lineTo(x1, y1)
////                    fluidPath.lineTo(x2, y2)
////                    fluidPath.lineTo(x1, y1)
////                }
////                fluidPath.close()
////                canvas.drawPath(fluidPath, fluidFlashPaint)
////                canvas.drawCircle(centerX, centerY, currentRadius * 0.45f, fluidFlashPaint)
////            }
////            return bitmap
////        }
////
////        // --- LAYER B: CONTINUOUS ASSETS ---
////        // 1. Falling Particles
////        particlePool.forEachIndexed { index, star ->
////            val horizontalSway = sin(runningTimeSec * 1.2f + star.seed) * 15f
////            val x = (star.startX * width + horizontalSway) % width
////            val y = (star.startY * height + star.speedY * runningTimeSec) % height
////
////            val calmBlink = sin(runningTimeSec * (2.0f + (index % 3)) + star.seed) * 0.5f + 0.5f
////            particlePaint.alpha = (calmBlink * 230).toInt().coerceIn(0, 255)
////
////            val finalX = if (x < 0) x + width else x
////            val finalY = if (y < 0) y + height else y
////            canvas.drawCircle(
////                finalX,
////                finalY,
////                star.baseSize * (0.5f + calmBlink * 0.5f),
////                particlePaint
////            )
////        }
////
////        // 2. Multi-Colored Butterflies (WIDER WING DESIGN)
////        butterflyPool.forEach { butterfly ->
////            val waveY = sin(runningTimeSec * (2.5f * butterfly.modifier) + butterfly.seed) * 65f
////            val x =
////                (butterfly.startX * width + (butterfly.speedX + 40f) * runningTimeSec) % (width + 200f) - 100f
////            val y = (butterfly.startY * height + waveY) % height
////
////            val wingFlap = abs(sin(runningTimeSec * 14.0f * butterfly.modifier + butterfly.seed))
////
////            canvas.save()
////            canvas.translate(x, y)
////
////            // FIX: Dynamically apply this butterfly's specific random color preset entry
////            butterflyPaint.color = butterfly.colorInt
////
////            val size = butterfly.baseSize
////
////            // FIX: Expanded width variable mapping parameter slightly (from 5.0f to 6.2f)
////            // to make the wings noticeably wider and fuller while keeping the vertical length small.
////            val leftWing = Path().apply {
////                moveTo(0f, 0f)
////                cubicTo(
////                    -size * 6.2f * wingFlap,
////                    -size * 2.2f,
////                    -size * 6.5f * wingFlap,
////                    size,
////                    0f,
////                    0f
////                )
////            }
////            canvas.drawPath(leftWing, butterflyPaint)
////
////            val rightWing = Path().apply {
////                moveTo(0f, 0f)
////                cubicTo(size * 6.2f * wingFlap, -size * 2.2f, size * 6.5f * wingFlap, size, 0f, 0f)
////            }
////            canvas.drawPath(rightWing, butterflyPaint)
////            canvas.drawOval(-1.2f, -size * 0.8f, 1.2f, size * 0.8f, bodyPaint)
////            canvas.restore()
////        }
////
////        // 3. Small Rising White Hearts
////        heartPool.forEach { heart ->
////            val swayX = sin(runningTimeSec * (2.0f * heart.modifier) + heart.seed) * 40f
////            val x = (heart.startX * width + swayX) % width
////            val y = (heart.startY * height - (heart.speedY + 35f) * runningTimeSec) % height
////
////            val finalY =
////                if (y < -100f) y + height + 200f else if (y > height + 100f) y - height - 100f else y
////
////            canvas.save()
////            canvas.translate(x, finalY)
////            val size = heart.baseSize
////
////            val heartPath = Path().apply {
////                moveTo(0f, -size / 4f)
////                cubicTo(-size * 2.2f, -size * 1.3f, -size * 2.4f, -size / 3f, 0f, size * 1.4f)
////                cubicTo(size * 2.4f, -size / 3f, size * 2.2f, -size * 1.3f, 0f, -size / 4f)
////            }
////            canvas.drawPath(heartPath, heartPaint)
////            canvas.restore()
////        }
////
////        return bitmap
////    }
////}

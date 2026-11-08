package com.example.atulyamvideocreatorapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Clock
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.OverlaySettings
import androidx.media3.effect.TextureOverlay
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultAssetLoaderFactory
import androidx.media3.transformer.DefaultDecoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import com.example.atulyamvideocreatorapp.model.EditableSlideItem
import com.example.atulyamvideocreatorapp.specialeffect.CustomAnimation
import com.example.atulyamvideocreatorapp.specialeffect.CustomOverlay
import com.example.atulyamvideocreatorapp.specialeffect.FumeSmokeTransformation
import com.example.atulyamvideocreatorapp.specialeffect.TwinkleParticleTransformation
import com.example.atulyamvideocreatorapp.speedcontroller.VideoSpeedPreset
import com.example.atulyamvideocreatorapp.userinterface.saveVideoToGallery
import com.example.atulyamvideocreatorapp.util.MovementPreset
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


sealed interface ExportState {
    object Idle : ExportState
    data class Processing(val progress: Float) : ExportState
    data class Success(val filePath: String) : ExportState
    data class Error(val message: String) : ExportState
}


@UnstableApi
class VideoCreatorViewModel : ViewModel() {
    // 1. This state holds the text value for the text field
    //Reactive single source of truth list holding all active slide states
    private val _slidesList = MutableStateFlow<List<EditableSlideItem>>(emptyList())
    val slidesList: StateFlow<List<EditableSlideItem>> = _slidesList.asStateFlow()

    // Tracks which slide index is currently highlighted for editing in the UI
    private val _selectedSlideIndex = MutableStateFlow(0)
    val selectedSlideIndex: StateFlow<Int> = _selectedSlideIndex.asStateFlow()
    private val _exportState = MutableStateFlow<ExportState>(
        ExportState.Idle
    )
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private var activeTransformer: Transformer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val progressHolder = ProgressHolder()

    // Dynamic UI visibility toggle flag states
    private val _showFluid = MutableStateFlow(true)
    val showFluid = _showFluid.asStateFlow()

    private val _showHearts = MutableStateFlow(true)
    val showHearts = _showHearts.asStateFlow()

    private val _showParticles = MutableStateFlow(true)
    val showParticles = _showParticles.asStateFlow()

    private val _showButterflies = MutableStateFlow(true)
    val showButterflies = _showButterflies.asStateFlow()

    fun toggleFluid(isActive: Boolean) {
        _showFluid.value = isActive
    }

    fun toggleHearts(isActive: Boolean) {
        _showHearts.value = isActive
    }

    fun toggleParticles(isActive: Boolean) {
        _showParticles.value = isActive
    }

    fun toggleButterflies(isActive: Boolean) {
        _showButterflies.value = isActive
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
            activeTransformer?.let { transformer ->
                val progressState = transformer.getProgress(progressHolder)
                if (progressState == Transformer.PROGRESS_STATE_AVAILABLE) {
                    _exportState.value = ExportState.Processing(progressHolder.progress.toFloat())
                }
                mainHandler.postDelayed(this, 250)
            }
        }
    }

    /**
     * FIX 1: THE CORE COMPILER FUNCTION (RETURNS List<EditedMediaItem> SYNCHRONOUSLY)
     * Maps slide parameters to the GPU effects layout. No coroutines here!
     */
    @SuppressLint("CheckResult")
    fun generateSlideshowTimeline(
        slidesTimelineList: List<EditableSlideItem>,
        baseTemplateDurationMs: Long,
        context: Context
    ): List<EditedMediaItem> {

        val standardSlideDurationMs =
            if (slidesTimelineList.isNotEmpty()) (baseTemplateDurationMs / slidesTimelineList.size) else 1

        // Explicitly returns the mapped track list collection down to caller
        return slidesTimelineList.mapIndexed { index, slide ->
            // Calculate play durations by applying individual speed presets
            //   val durationPerImageMs = (standardSlideDurationMs / slide.selectedSpeed.factor).toLong()
            val durationPerImageMs = (standardSlideDurationMs * slide.selectedSpeed.factor).toLong()
            require(durationPerImageMs > 0L) { "Duration per image must be greater than zero" }

            // Target the actual photo URI from the active user slide selection model
            val imageMediaItem = MediaItem.Builder()
                .setUri(slide.imageUri)
                .setImageDurationMs(durationPerImageMs)
                .build()

            val spatialPreset = slide.selectedAnimation
            val baseMovementEffects = spatialPreset.getEffects(durationPerImageMs)


            // 💡 STEP 1: Keep the bitmap canvas size small and tight to just wrap your text bounds.
// This prevents high RAM overhead and guarantees crisp rendering.
            val canvasWidth = 1080
            val canvasHeight = 250 // Clean, localized height boundary box layer

            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 54f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                setShadowLayer(10f, 0f, 4f, Color.parseColor("#E53935")) // Red shadow outline glow
            }

            val bulletproofOverlay = object : BitmapOverlay() {

                override fun getBitmap(presentationTimeUs: Long): Bitmap {
                    val absoluteTimeMs = presentationTimeUs / 1_000f
                    val totalSlideTimeMs = durationPerImageMs.toFloat()
                    val currentTimeMs = absoluteTimeMs % totalSlideTimeMs

                    // Build a tight horizontal strip canvas matching the text boundary heights
                    val frameBitmap =
                        Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(frameBitmap)

                    val activeCaption = slidesTimelineList[index].caption ?: ""
                    if (activeCaption.isBlank()) {
                        return frameBitmap
                    }

                    // Proportional fade timelines
                    val fadeInEnd = totalSlideTimeMs * 0.15f
                    val fadeOutStart = totalSlideTimeMs * 0.85f
                    val fadeOutDuration = totalSlideTimeMs - fadeOutStart

                    val currentAlphaScale: Float = when {
                        currentTimeMs < fadeInEnd -> currentTimeMs / fadeInEnd
                        currentTimeMs >= fadeInEnd && currentTimeMs < fadeOutStart -> 1f
                        else -> {
                            val exitProgress = (currentTimeMs - fadeOutStart) / fadeOutDuration
                            (1f - exitProgress).coerceIn(0f, 1f)
                        }
                    }
                    val alpha255 = (currentAlphaScale * 255).toInt().coerceIn(0, 255)

                    val typingEndTime = totalSlideTimeMs * 0.65f
                    val typewriterProgress = (currentTimeMs / typingEndTime).coerceIn(0f, 1f)
                    val totalChars = activeCaption.length
                    val charsToShow =
                        (typewriterProgress * totalChars).toInt().coerceIn(0, totalChars)
                    val partialText = activeCaption.substring(0, charsToShow)

                    // 💡 STEP 2: Draw the text directly in the horizontal middle and vertical center of this tight canvas strip
                    val currentX = canvasWidth / 2f
                    val currentY =
                        (canvasHeight / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

                    if (alpha255 > 0) {
                        textPaint.alpha = alpha255
                        canvas.drawText(partialText, currentX, currentY, textPaint)
                    }

                    return frameBitmap
                }

                override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
                    // 💡 STEP 3: USE THE OPENGL GRID MATRIX TO POSITION THE TEXT PERFECTLY AT 0.60f
                    // X = 0f keeps it perfectly centered horizontally.
                    // Y = 0.20f positions it exactly 60% down from the screen top (-1f is top, 0f is middle, +1f is bottom).
                    // ⚠️ Media3 bug note: Multiply by -1f if your version requires inverted frame anchors.
                    return OverlaySettings.Builder()
                        .setAlphaScale(1f)
                        .setOverlayFrameAnchor(0f, 0f)    // Target the center of our text sticker
                        .setBackgroundFrameAnchor(
                            0f,
                            -0.10f
                        ) // Place it exactly at the 60% mark down the video canvas
                        .build()
                }
            }


            //   val textOverlay = TextOverlay.createStaticTextOverlay(spannable)
            //     ImmutableList.of(OverlayEffect(ImmutableList.of(textOverlay) as List<TextureOverlay>))


            val finalCombinedEffects = mutableListOf<androidx.media3.common.Effect>().apply {
                // Layer 1: Core matrix movement effects (Zoom, Left/Right Pan, Spins, Shakes)
                addAll(baseMovementEffects)

                add(androidx.media3.effect.OverlayEffect(ImmutableList.of(bulletproofOverlay) as List<TextureOverlay>))
                // 1. Safe Null Check Isolation

                // FIX: Standardise the video frame resolution grid layout on the GPU thread!
                // This forces standard vertical cropping limits so short photos don't cut off your overlays!
                val fullScreenStandardizer =
                    androidx.media3.effect.Presentation.createForWidthAndHeight(
                        1080,  // target width
                        1920,  // target height (9:16 aspect ratio bounds)
                        androidx.media3.effect.Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP
                    )
                add(fullScreenStandardizer)

                // Layer 2: Optional Horizontal Fluid Wipes entrance burst
                if (_showFluid.value) {
                    val flashData =
                        CustomAnimation.FluidFlashTransformation(totalDurationMs = durationPerImageMs)
                    add(
                        androidx.media3.effect.OverlayEffect(
                            ImmutableList.of(
                                CustomOverlay(
                                    context = context,
                                    animation = flashData,
                                    isFluidActive = _showFluid.value,
                                    isHeartsActive = _showHearts.value,
                                    isParticlesActive = _showParticles.value,
                                    isButterfliesActive = _showButterflies.value
                                )
                            ) as List<TextureOverlay>
                        )
                    )
                }

                // Layer 3: Global continuous running multi-color assets layer (Particles, Butterflies, Hearts)
                val globalAssetData =
                    TwinkleParticleTransformation(totalDurationMs = durationPerImageMs)
                add(
                    androidx.media3.effect.OverlayEffect(
                        ImmutableList.of(
                            CustomOverlay(
                                context = context,
                                animation = globalAssetData,
                                isFluidActive = _showFluid.value,
                                isHeartsActive = _showHearts.value,
                                isParticlesActive = _showParticles.value,
                                isButterfliesActive = _showButterflies.value
                            )
                        ) as List<TextureOverlay>
                    )
                )

                // Layer 4: Optional Fume Smoke wave inject layers for indices 11 & 12
                if (index % 15 == 11 || index % 15 == 12) {
                    val fumeData = FumeSmokeTransformation(totalDurationMs = durationPerImageMs)
                    val smokeOverlay = CustomOverlay(context, fumeData)
                    add(androidx.media3.effect.OverlayEffect(ImmutableList.of(smokeOverlay) as List<TextureOverlay>))
                }
            }

            EditedMediaItem.Builder(imageMediaItem)
                .setDurationUs(durationPerImageMs * 1_000L)
                .setFrameRate(30)
                .setEffects(androidx.media3.transformer.Effects(listOf(), finalCombinedEffects))
                .build()
        }
    }

    /**
     * FIX 2: THE EXPORT MANAGER (RUNS ASYNC TASK inside Dispatchers.IO)
     * Calls the timeline generator, mixes the audio track sequence, and starts transcoding.
     */
    fun exportMultiImageBhaktiStatus(
        context: Context,
        slidesTimelineList: List<EditableSlideItem>,
        baseTemplateDurationMs: Long,
        audioUri: Uri
    ) {
        if (slidesTimelineList.isEmpty()) {
            _exportState.value = ExportState.Error("No images selected")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            try {
                _exportState.value = ExportState.Processing(0f)

                val outputDir = File(context.cacheDir, "exports").apply { mkdirs() }
                val outputFile = File(outputDir, "Bhakti_Status_${System.currentTimeMillis()}.mp4")
                if (outputFile.exists()) outputFile.delete()

                // A. Recompile structural video track sequence
                val editedImageItems =
                    generateSlideshowTimeline(slidesTimelineList, baseTemplateDurationMs, context)
                val imageTrackSequence = EditedMediaItemSequence(editedImageItems)

                // B. Recompile dynamic audio mixer segment row track path
                val audioMediaItem = MediaItem.fromUri(audioUri)
                val editedAudioItem = EditedMediaItem.Builder(audioMediaItem).build()
                val audioTrackSequence = EditedMediaItemSequence(listOf(editedAudioItem))

                // C. Assemble sequential layers together into final track layout composition
                val composition =
                    Composition.Builder(listOf(imageTrackSequence, audioTrackSequence)).build()

                val decoderFactory = DefaultDecoderFactory(context.applicationContext)
                val assetLoaderFactory = DefaultAssetLoaderFactory(
                    context.applicationContext,
                    decoderFactory,
                    Clock.DEFAULT
                )

                val transformer = Transformer.Builder(context.applicationContext)
                    .setAssetLoaderFactory(assetLoaderFactory)
                    .setVideoMimeType(androidx.media3.common.MimeTypes.VIDEO_H264)
                    .setAudioMimeType(androidx.media3.common.MimeTypes.AUDIO_AAC)
                    .build()

                activeTransformer = transformer

                mainHandler.post {
                    transformer.addListener(object : Transformer.Listener {
                        override fun onCompleted(
                            composition: Composition,
                            exportResult: ExportResult
                        ) {
                            mainHandler.removeCallbacks(progressRunnable)
                            activeTransformer = null
                            val galleryUri = saveVideoToGallery(context, outputFile)
                            _exportState.value =
                                ExportState.Success(
                                    galleryUri?.toString() ?: outputFile.absolutePath
                                )
                        }

                        override fun onError(
                            composition: Composition,
                            exportResult: ExportResult,
                            exportException: ExportException
                        ) {
                            mainHandler.removeCallbacks(progressRunnable)
                            activeTransformer = null
                            _exportState.value = ExportState.Error(
                                exportException.localizedMessage ?: "GPU rendering pipeline failed"
                            )
                        }
                    })
                    mainHandler.post(progressRunnable)
                }
                transformer.start(composition, outputFile.absolutePath)
            } catch (e: Exception) {
                mainHandler.removeCallbacks(progressRunnable)
                activeTransformer = null
                _exportState.value =
                    ExportState.Error(e.localizedMessage ?: "Transcoding initialization failure")
            }
        }
    }

    fun cancelActiveExport() {
        mainHandler.removeCallbacks(progressRunnable)
        activeTransformer?.cancel()
        activeTransformer = null
        _exportState.value = ExportState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveExport()
    }

    /**
     * 1. HOOK FOR IMAGE PICKING SELECTION:
     * Merges newly selected image URIs with existing configurations to hold and preserve state.
     */
    fun updateSelectedImages(newUris: List<Uri>) {
        val currentList = _slidesList.value
        val mergedList = mutableListOf<EditableSlideItem>()

        newUris.forEachIndexed { index, uri ->
            if (index < currentList.size) {
                // PRESERVE STATE: Keep the previous user modifications if this image position existed
                mergedList.add(currentList[index].copy(imageUri = uri))
            } else {
                // INITIALIZE NEW ENTRIES: Assign sequential default presets out-of-the-box
                mergedList.add(
                    EditableSlideItem(
                        id = "slide_${index}_${System.currentTimeMillis()}",
                        imageUri = uri,
                        selectedAnimation = MovementPreset.fromIndex(index),
                        selectedSpeed = VideoSpeedPreset.NORMAL
                    )
                )
            }
        }
        _slidesList.value = mergedList
    }

    /**
     * 2. CONTROLLER UPDATE HOOK:
     * Modifies the animation style and speed of a specific slide without altering any others.
     */
    fun updateSlideSettings(
        index: Int,
        updatedAnimation: MovementPreset,
        updatedSpeed: VideoSpeedPreset,
        caption: String
    ) {
        val currentList = _slidesList.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(
                selectedAnimation = updatedAnimation,
                selectedSpeed = updatedSpeed,
                caption = caption
            )
            _slidesList.value = currentList
        }
    }

    fun setSelectedSlideIndex(index: Int) {
        _selectedSlideIndex.value = index
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    val slideList1 = mutableStateListOf<EditableSlideItem>()
    fun removeSlide(slideId: String) {
        // Removes the matching slide object. Compose will notice this and refresh the UI instantly.
        val currentList = _slidesList.value.toMutableList()
        currentList.removeAll { it.id == slideId }
        _slidesList.value = currentList
    }
}



package com.example.atulyamvideocreatorapp.userinterface

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.transformer.EditedMediaItem
import coil.compose.AsyncImage
import com.example.atulyamvideocreatorapp.R
import com.example.atulyamvideocreatorapp.viewmodel.VideoCreatorViewModel

@OptIn(UnstableApi::class)
@Composable
fun BhaktiAppMainScreen(viewModel: VideoCreatorViewModel, modifier: Modifier) {
    //for check boxes
    val fluidState by viewModel.showFluid.collectAsState()
    val heartsState by viewModel.showHearts.collectAsState()
    val particlesState by viewModel.showParticles.collectAsState()
    val butterflyState by viewModel.showButterflies.collectAsState()
    // State to hold the selected image URI
    var selectedImageUri by remember { mutableStateOf<List<Uri>?>(emptyList()) }
    val listOfImagesSelected = remember { mutableListOf<Uri>() }
    var isShowCheckBox by remember { mutableStateOf(false) }
    val slidesList by viewModel.slidesList.collectAsState()
    val selectedIndex by viewModel.selectedSlideIndex.collectAsState()
    val activeSlide = slidesList.getOrNull(selectedIndex)

    // val context = LocalContext.current
    // 1. Fetch the platform Android Context
    val context = androidx.compose.ui.platform.LocalContext.current
    val baseTemplateDurationEditable: Long = slidesList.size.times(1000L)
    // 2. Initialize and remember the ExoPlayer instance variable securely
    val exoPlayerInstance = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            // Optional: Set default playback configurations
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    // 3. SECURE CLEANUP: Automatically releases hardware GPU codecs when the user leaves this screen
    DisposableEffect(exoPlayerInstance) {
        onDispose {
            exoPlayerInstance.release()
        }
    }
    // Sample template data
    val audioUri = "android.resource://${context.packageName}/${R.raw.music4}".toUri()

    /**
     * Live-updates the active ExoPlayer instance with a newly recompiled slideshow composition timeline.
     * Resolves compilation errors by utilizing standard .add() hooks and injecting global video effects chains.
     */
    @OptIn(UnstableApi::class)
    fun updateSlideshowCompositionPipeline(
        context: Context,
        player: ExoPlayer?,
        refreshedTimeline: List<EditedMediaItem>
    ) {
        val activePlayer = player ?: return

        // 1. FIX: CRITICAL EMPTY LIST GUARD CLAUSE
        // If no slides are currently configured or loaded, do nothing to prevent the concatenating crash!
        if (refreshedTimeline.isEmpty()) {
            activePlayer.stop()
            activePlayer.clearMediaItems()
            return
        }

        // 1. Capture current playback position coordinates to prevent jarring video rewinds
        val currentWindowIndex = activePlayer.currentMediaItemIndex
        val currentPlaybackPositionMs = activePlayer.currentPosition
        val wasPlaying = activePlayer.isPlaying

        // 2. Clear out older track cache configurations off the thread context
        activePlayer.stop()
        activePlayer.clearMediaItems()

        // 3. Assemble sequential image tracks cleanly inside a Concatenating block container
        val sequenceSourceBuilder = ConcatenatingMediaSource2.Builder()
        val allVideoEffects = mutableListOf<androidx.media3.common.Effect>()

        refreshedTimeline.forEach { editedMediaItem ->
            val baseMediaItem = editedMediaItem.mediaItem
            val itemEffects = editedMediaItem.effects

            // Gather all visual overlays (particles, flowers, butterflies) to pass globally to the player pipeline
            allVideoEffects.addAll(itemEffects.videoEffects)

            // Create standard media source segment
            val playableSegmentSource: MediaSource =
                androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context.applicationContext)
                    .createMediaSource(baseMediaItem)

            // FIX 1: Use the standard native .add(...) method exposed by ConcatenatingMediaSource2
            sequenceSourceBuilder.add(
                playableSegmentSource, /* index= */
                baseTemplateDurationEditable
            )
        }

        val finalLiveTimelineCompositionSource = sequenceSourceBuilder.build()

        // 4. Force the underlying player engine to load the updated asset track structure
        activePlayer.setMediaSource(finalLiveTimelineCompositionSource)

        // FIX 2: Set your complete video effects array directly onto the player instance!
        // This tells the active hardware surface to process overlays live during preview loops
        activePlayer.setVideoEffects(allVideoEffects)

        activePlayer.prepare()

        // 5. Instantly resume playback context exactly where the user left off
        activePlayer.seekTo(currentWindowIndex, currentPlaybackPositionMs)
        if (wasPlaying) {
            activePlayer.play()
        }
    }
    // Photo picker launcher
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(60)
    ) { uri ->
        // Callback executed when user selects an image
        selectedImageUri = uri

        if (listOfImagesSelected.size <= 60) selectedImageUri?.let {
            listOfImagesSelected.addAll(
                uri
            )

        }

        // Inside your gallery picture picker callback hook listener:
        val imageUrisFromPicker =
            listOfImagesSelected // Dynamic array list from Android PhotoPicker

// This automatically updates, expands, or merges images while retaining all custom modifications!
        viewModel.updateSelectedImages(imageUrisFromPicker)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Select a Floating/Opening Screen",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 3.dp)
            )
            Icon(
                modifier = Modifier.clickable { isShowCheckBox = !isShowCheckBox },
                imageVector = Icons.Filled.Settings,
                contentDescription = "setting"
            )
        }

        if (!isShowCheckBox) {
            SlideEditorDashboard(viewModel)
        } else {
            EffectsToggleDashboard(
                showFluid = fluidState,
                showHearts = heartsState,
                showParticles = particlesState,
                showButterflies = butterflyState,
                onToggleChanged = { key, isChecked ->
                    when (key) {
                        "fluid" -> viewModel.toggleFluid(isChecked)
                        "hearts" -> viewModel.toggleHearts(isChecked)
                        "particles" -> viewModel.toggleParticles(isChecked)
                        "butterflies" -> viewModel.toggleButterflies(isChecked)
                    }
                }
            )

            // Re-generate your active timeline track composition right here to update the player preview instantly!
            val refreshedTimeline = viewModel.generateSlideshowTimeline(
                slidesList,
                baseTemplateDurationEditable, context
            )

            updateSlideshowCompositionPipeline(
                context,
                player = exoPlayerInstance, refreshedTimeline
            )

        }

        // Button to trigger the system image picker
        Button(onClick = {
            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Open Gallery")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Optional: Show preview of the selected image using Coil
        if (selectedImageUri?.isNotEmpty() == true) {
            viewModel.selectedSlideIndex.collectAsState().value.let { uri ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = listOfImagesSelected[uri],
                        contentDescription = "Selected devotional image",
                        modifier = Modifier
                            .size(180.dp)
                            .padding(8.dp)
                    )
                    if (activeSlide?.caption?.isNotEmpty() == true)
                        Text(
                            text = activeSlide.caption,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.7f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Log.d("TAG", "BhaktiAppMainScreen1: ${listOfImagesSelected.size}")
        // Embed the previous creator screen layout
        BhaktiCreatorScreen(
            viewModel = viewModel,
            selectedImageUri = listOfImagesSelected,
            baseTemplateDurationEditable = baseTemplateDurationEditable,
            audioUriEditable = audioUri
        )
    }

}
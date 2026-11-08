package com.example.atulyamvideocreatorapp.userinterface

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.example.atulyamvideocreatorapp.viewmodel.ExportState
import com.example.atulyamvideocreatorapp.viewmodel.VideoCreatorViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun BhaktiCreatorScreen(
    viewModel: VideoCreatorViewModel,
    selectedImageUri: List<Uri>?,
    baseTemplateDurationEditable: Long = 40000L,
    audioUriEditable: Uri
) {
    val context = LocalContext.current
    val exportState by viewModel.exportState.collectAsState()
    val slidesList by viewModel.slidesList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Video",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Handle states dynamically based on ViewModel flow
        when (val state = exportState) {
            is ExportState.Idle -> {
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            viewModel.exportMultiImageBhaktiStatus(
                                context,
                                slidesTimelineList = slidesList,
                                baseTemplateDurationMs = baseTemplateDurationEditable,
                                audioUri = audioUriEditable
                            )   
                        }
                    },
                    enabled = selectedImageUri != null
                ) {
                    Text("Render Devotional Video")
                }
            }

            is ExportState.Processing -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Blending Devotional Audio & Visuals...")
            }

            is ExportState.Success -> {
                Text("✨ Video Saved Successfully! ✨", color = MaterialTheme.colorScheme.primary)
                Text("Path: ${state.filePath}", style = MaterialTheme.typography.bodySmall)
                // You can add a Share Button here using standard Android Intents
                Button(onClick = {viewModel.resetExportState()}){
                    Text("Re-Create More videos")
                }

            }

            is ExportState.Error -> {
                Text("Export Failed: ${state.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = {viewModel.resetExportState()}){
                    Text("Try Again.")
                }
            }
        }
    }
}

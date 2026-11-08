package com.example.atulyamvideocreatorapp.userinterface

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.atulyamvideocreatorapp.speedcontroller.VideoSpeedPreset
import com.example.atulyamvideocreatorapp.util.MovementPreset
import com.example.atulyamvideocreatorapp.viewmodel.VideoCreatorViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideEditorDashboard(
    viewModel: VideoCreatorViewModel,
    modifier: Modifier = Modifier,
) {
    val slidesList by viewModel.slidesList.collectAsState()
    val selectedIndex by viewModel.selectedSlideIndex.collectAsState()
    val activeSlide = slidesList.getOrNull(selectedIndex)

    var rememberCaptionText by remember { mutableStateOf("") }
    var speedPresetIsClicked by remember { mutableStateOf(false) }


// Core states for calculation
    var totalMs by remember { mutableLongStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isMasterActive by remember { mutableStateOf(false) }

    // Custom timing input state
    var customInput by remember { mutableStateOf("") }

    LaunchedEffect(key1 = isTimerRunning, key2 = isMasterActive) {
        while (isTimerRunning && isMasterActive) {
            var lastTimestamp = System.currentTimeMillis()
            while (true) {
                delay(100L.milliseconds) // ~60 FPS update frequency
                val currentTimestamp = System.currentTimeMillis()
                val delta = currentTimestamp - lastTimestamp
                totalMs += delta
                lastTimestamp = currentTimestamp
                Log.d(
                    "TAG",
                    "AdvancedTimerScreen: $currentTimestamp $currentTimestamp $delta $totalMs"
                )
            }
        }
    }
    val formattedTime = remember(totalMs) {
        val minutes = (totalMs / 60000) % 60
        val seconds = (totalMs / 1000) % 60
        val ms = (totalMs%1000) / 100
        String.format("%01d:%02d:%01d", minutes, seconds, ms)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF141414)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // --- SECTION 1: TIMELINE THUMBNAILS HORIZONTAL ROW ---
      Row(horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              text = "Slides Timeline Track:",
              color = Color.Gray,
              fontSize = 12.sp,
              modifier = Modifier.padding(start = 12.dp)
          )
          // --- 3. MASTER BUTTON (COMPLETELY START OR STOP/RESET) ---
          Button(
              onClick = {
                  if (!isMasterActive) {
                      // Turn system fully ON
                      isMasterActive = true
                      isTimerRunning = true
                  } else {
                      // Turn system fully OFF & Reset calculations
                      isMasterActive = false
                      isTimerRunning = false
                      totalMs = 0L
                  }
              },
              colors = ButtonDefaults.buttonColors(
                  containerColor = if (!isMasterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
              ),
              modifier = Modifier
                  .height(21.dp)
          ) {
              Text(
                  text = if (!isMasterActive) "START" else "STOP/RESET",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
              )
          }
      }
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(slidesList) { idx, slide ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                val isSelected = idx == selectedIndex
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFF69B4) else Color.DarkGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            rememberCaptionText = activeSlide?.caption ?: ""
                            viewModel.setSelectedSlideIndex(idx)
                            totalMs = 0L

//                            viewModel.updateSlideSettings(
//                                selectedIndex,
//                                activeSlide?.selectedAnimation,
//                                itemText,
//                                activeSlide?.caption.toString()
//                            )
                        }
                ) {
//                Column(
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
                    AsyncImage(
                        model = slide.imageUri,
                        contentDescription = "Slide Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black, shape = RoundedCornerShape(8.dp))
                    )
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete slide",
//                        tint = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.clickable{
//                            viewModel.removeSlide(slide.id)
//                        }.size(16.dp)
//                    )
                    // }
                }
              //  if (isMasterActive) {
                    Text(
                        text = formattedTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
     //   }
    }
    Spacer(modifier = Modifier.height(16.dp))

    // --- SECTION 2: DUAL ADJUSTMENT CONTROLS FOR ACTIVE SLIDE ---
    activeSlide?.let { it1 ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = "Configure Slide #${selectedIndex + 1}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // A. ANIMATION DROPDOWN CHANGER
            Text(
                "Movement Animation Style:",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            var isMenuExpanded by remember { mutableStateOf(false) }
            val animationOptions = remember {
                listOf(
                    Pair("Gentle Zoom In", MovementPreset.GentleZoomIn),
                    Pair("Subtle Zoom Out", MovementPreset.SubtleZoomOut),
                    Pair("Pan Across Right", MovementPreset.HorizontalRightPan),
                    Pair("Pan Across Left", MovementPreset.HorizontalLeftPan),
                    Pair("Pan Across Top", MovementPreset.VerticalTopPan),
                    Pair("Pan Across Bottom", MovementPreset.VerticalBottomPan),
                    Pair("Tilt Clockwise", MovementPreset.AntiClockwiseTilt),
                    Pair("Tilt Anti-Clockwise", MovementPreset.ClockwiseTilt),
                    Pair("Cinematic SlowGlideCombo", MovementPreset.CinematicSlowGlideCombo),
                    Pair("Elegant DriftAndFadeIn", MovementPreset.ElegantDriftAndFadeIn),
                    Pair("Cinematic Slow Glide", MovementPreset.CinematicSlowGlideCombo),
                    Pair("360 Smooth Spin", MovementPreset.AntiClockAndClockwiseRotation)
                )
            }
          //  AdvancedTimerScreen()
            val currentLabel =
                animationOptions.firstOrNull { it.second.javaClass == activeSlide.selectedAnimation.javaClass }?.first
                    ?: "Select Animation"

            ExposedDropdownMenuBox(
                expanded = isMenuExpanded,
                onExpandedChange = { isMenuExpanded = !isMenuExpanded }
            ) {
                OutlinedTextField(
                    value = currentLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF69B4),
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.background(Color(0xFF2B2B2B))
                ) {
                    animationOptions.forEach { (label, preset) ->
                        DropdownMenuItem(
                            text = { Text(label, color = Color.White) },
                            onClick = {
                                viewModel.updateSlideSettings(
                                    selectedIndex,
                                    preset,
                                    activeSlide.selectedSpeed,
                                    activeSlide.caption
                                )
                                isMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = rememberCaptionText,
                onValueChange = { newText ->
                    rememberCaptionText = newText
                    viewModel.updateSlideSettings(
                        selectedIndex,
                        activeSlide.selectedAnimation,
                        activeSlide.selectedSpeed,
                        newText
                    )
                },
                label = { Text("Caption for this slide") },
                textStyle = TextStyle(color = Color(0xFFFFFFFF)),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // B. TEMPO SEGMENTED SPEED CONTROLLER
            Text(
                "Slide Duration Pace Speed:",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color(0x12FFFFFF), shape = RoundedCornerShape(21.dp))
                    .border(1.dp, Color(0x22FFFFFF), shape = RoundedCornerShape(21.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Show Caption Overlay Preview
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    contentPadding = PaddingValues(
                        horizontal = 20.dp
                    ), // Padding at start and end of scroll
                ) {
                    val click =
                        if (!speedPresetIsClicked) VideoSpeedPreset.getSingleClickFeatures() else VideoSpeedPreset.getDoubleClickFeatures()
                    items(click) { itemText ->
                        val isSpeedSelected = itemText == activeSlide.selectedSpeed
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (isSpeedSelected) Color.White else Color.Transparent,
                                    RoundedCornerShape(17.dp)
                                )
                                .combinedClickable(
                                    onClick = {
                                        viewModel.updateSlideSettings(
                                            selectedIndex,
                                            activeSlide.selectedAnimation,
                                            itemText,
                                            activeSlide.caption
                                        )

                                    },
                                    onDoubleClick = {
                                        speedPresetIsClicked = !speedPresetIsClicked
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                text = itemText.label ,
                                color = if (isSpeedSelected) Color.Black else Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = if (isSpeedSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            }
                    }
                }
            }
        }
    }
}

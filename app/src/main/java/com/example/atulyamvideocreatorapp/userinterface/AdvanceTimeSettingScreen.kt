package com.example.atulyamvideocreatorapp.userinterface

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("DefaultLocale")
@Composable
fun AdvancedTimerScreen(
    onSlideClick: () -> Unit,
   // onMainTimerStartStop: () -> Unit,
    formatedDuration: String,
    perSlideDuration: Long
) {
    // Core states for calculation
    var totalMs by remember { mutableLongStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isMasterActive by remember { mutableStateOf(false) }

    // Custom timing input state
    var customInput by remember { mutableStateOf("") }

    // Coroutine effect that drives the time calculation safely
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
    Log.d("TAG", "AdvancedTimerScreen: saf $totalMs")
    // Helper to format raw seconds into a structured string (HH:MM:SS)
    val formattedTime = remember(totalMs) {
        val minutes = (totalMs / 60000) % 60
        val seconds = (totalMs / 1000) % 60
        val ms = totalMs % 1000
        String.format("%02d:%02d:%03d", minutes, seconds, ms)
    }

    // --- 1. CLICKABLE AREA TO PAUSE AND RESUME ---


    Box(
        modifier = Modifier
            .clickable(enabled = isMasterActive) {
                // Click toggles calculation stream seamlessly without wiping existing progress
                //     isTimerRunning = !isTimerRunning
                totalMs = 0L
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedTime,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isMasterActive) {
                Text(
                    text = if (isTimerRunning) " Tap to Pause " else " Tap to Resume ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }

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
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (!isMasterActive) "START" else "STOP/RESET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
package com.example.atulyamvideocreatorapp.userinterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EffectsToggleDashboard(
    showFluid: Boolean,
    showHearts: Boolean,
    showParticles: Boolean,
    showButterflies: Boolean,
    onToggleChanged:  (type: String, isChecked: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        Text(
            text = "Video Overlay Layers Settings:",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Create a helper grid checklist row loop helper
        val overlayOptions = listOf(
            Triple("Fluid Transition Flash", showFluid, "fluid"),
            Triple("Solid Colourful Hearts", showHearts, "hearts"),
            Triple("Twinkling Falling Stars", showParticles, "particles"),
            Triple("Floating White Butterflies", showButterflies, "butterflies")
        )

        overlayOptions.forEach { (label, currentState, key) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Checkbox(
                        checked = currentState,
                        onCheckedChange = { isChecked -> onToggleChanged(key, isChecked) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFF69B4), // Beautiful Pink Accent Color
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                Text(
                    text = label,
                    color = if (currentState) Color.White else Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

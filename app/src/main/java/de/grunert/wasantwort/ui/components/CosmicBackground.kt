package de.grunert.wasantwort.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import de.grunert.wasantwort.ui.theme.GlassBackground

/**
 * Cosmic Background mit radialen Gradients für Tiefe
 * Macht den Glass-Effekt sichtbar
 */
@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                // Base dark background
                GlassBackground
            )
    ) {
        // Orb 1: Top-Left (violet/blau)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1200f,
                        colors = listOf(
                            Color(0xFF2D1B4E).copy(alpha = 0.15f), // Violet
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 2: Center-Right (blue)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1200f, 600f),
                        radius = 1000f,
                        colors = listOf(
                            Color(0xFF1B2D4E).copy(alpha = 0.12f), // Blue
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 3: Bottom-Left (subtle gray)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 1600f),
                        radius = 800f,
                        colors = listOf(
                            Color(0xFF2A2A3E).copy(alpha = 0.10f), // Gray
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

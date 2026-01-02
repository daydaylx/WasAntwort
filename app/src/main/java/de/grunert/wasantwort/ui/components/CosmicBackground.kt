package de.grunert.wasantwort.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import de.grunert.wasantwort.ui.theme.GlassBackground
import kotlin.math.sin
import kotlin.random.Random

/**
 * Cosmic Background mit radialen Gradients und animierten Sternen.
 * Macht den Glass-Effekt sichtbar.
 */
@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier
) {
    // Animation driver for twinkling
    val infiniteTransition = rememberInfiniteTransition(label = "star_twinkle")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val stars = remember {
        val random = Random(42)
        List(70) {
            Star(
                x = random.nextFloat(),
                y = random.nextFloat(),
                radius = 0.6f + random.nextFloat() * 1.4f,
                baseAlpha = 0.1f + random.nextFloat() * 0.15f,
                phase = random.nextFloat() * 2f * Math.PI.toFloat(),
                speed = 0.5f + random.nextFloat() // Different blinking speeds
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                // Base dark background
                GlassBackground
            )
    ) {
        // Subtle vertical gradient to add depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B1020).copy(alpha = 0.25f),
                            Color.Transparent,
                            Color(0xFF0A0A14).copy(alpha = 0.35f)
                        )
                    )
                )
        )

        // Orb 1: Top-Left (cool blue)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1200f,
                        colors = listOf(
                            Color(0xFF1B2B4F).copy(alpha = 0.18f), // Blue
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 2: Center-Right (teal)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1200f, 600f),
                        radius = 1000f,
                        colors = listOf(
                            Color(0xFF0F3B3F).copy(alpha = 0.16f), // Teal
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 3: Bottom-Left (deep blue)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 1600f),
                        radius = 800f,
                        colors = listOf(
                            Color(0xFF1A2236).copy(alpha = 0.16f), // Deep blue
                            Color.Transparent
                        )
                    )
                )
        )

        // Orb 4: Bottom-Right (soft cyan)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1200f, 1600f),
                        radius = 900f,
                        colors = listOf(
                            Color(0xFF0F2A33).copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                // Calculate dynamic alpha based on sine wave
                // sin returns -1..1, mapped to slightly modulate the base alpha
                val wave = sin(animationProgress * star.speed + star.phase)
                // Modulate alpha by +/- 20% of its base value, clamped between 0.05 and 1.0
                val currentAlpha = (star.baseAlpha + (wave * 0.08f)).coerceIn(0.05f, 1.0f)

                drawCircle(
                    color = Color.White.copy(alpha = currentAlpha),
                    radius = star.radius,
                    center = Offset(star.x * size.width, star.y * size.height)
                )
            }
        }
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val baseAlpha: Float,
    val phase: Float,
    val speed: Float
)

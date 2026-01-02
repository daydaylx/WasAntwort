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
        List(40) {
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
                // Base dark background - Tiefes Blau-Violett
                Color(0xFF0F0C24)
            )
    ) {
        // Subtle vertical gradient to add depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A103C).copy(alpha = 0.30f),
                            Color.Transparent,
                            Color(0xFF0A0A14).copy(alpha = 0.40f)
                        )
                    )
                )
        )

        // Orb 1: Top-Left (Vibrant Purple/Blue)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1400f,
                        colors = listOf(
                            Color(0xFF4D3B8F).copy(alpha = 0.25f), 
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 2: Center-Right (Teal/Cyan pop)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1200f, 800f),
                        radius = 1100f,
                        colors = listOf(
                            Color(0xFF00A896).copy(alpha = 0.18f), 
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Orb 3: Bottom-Left (Deep Indigo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0f, 1800f),
                        radius = 1000f,
                        colors = listOf(
                            Color(0xFF2E2459).copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Orb 4: Top-Right (Magenta/Pink Highlight - NEW for contrast)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1400f, 100f),
                        radius = 900f,
                        colors = listOf(
                            Color(0xFFB5179E).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Orb 5: Bottom-Right (Soft Cyan)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(1200f, 1600f),
                        radius = 900f,
                        colors = listOf(
                            Color(0xFF0F2A33).copy(alpha = 0.18f),
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
                    color = Color(0xFFE8E0FF).copy(alpha = currentAlpha),
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


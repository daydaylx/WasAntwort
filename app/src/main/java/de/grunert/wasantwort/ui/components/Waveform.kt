package de.grunert.wasantwort.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.ui.theme.Accent1
import de.grunert.wasantwort.ui.theme.Accent2
import kotlin.math.sin

@Composable
fun LoadingWaveform(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_animation")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            
            // Draw 3 overlapping waves
            val waves = listOf(
                Triple(Accent1, 1.0f, 0f),
                Triple(Accent2, 0.8f, 2f),
                Triple(Color.White, 0.5f, 4f)
            )

            waves.forEach { (color, amplitudeScale, phaseOffset) ->
                val path = Path()
                path.moveTo(0f, centerY)

                for (x in 0..width.toInt() step 5) {
                    val xFloat = x.toFloat()
                    // Sine wave formula: y = A * sin(kx + wt + phi)
                    // Envelope function to dampen edges
                    val envelope = sin((xFloat / width) * Math.PI).toFloat()
                    
                    val y = centerY + 
                            sin((xFloat / 60f) + phase + phaseOffset) * 
                            30f * amplitudeScale * envelope

                    path.lineTo(xFloat, y)
                }

                drawPath(
                    path = path,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    ),
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0f),
                            color.copy(alpha = 0.8f),
                            color.copy(alpha = 0f)
                        )
                    )
                )
            }
        }
    }
}

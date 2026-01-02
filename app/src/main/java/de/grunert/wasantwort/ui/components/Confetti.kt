package de.grunert.wasantwort.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import de.grunert.wasantwort.ui.theme.Accent1
import de.grunert.wasantwort.ui.theme.Accent2
import de.grunert.wasantwort.ui.theme.GlowPrimary
import de.grunert.wasantwort.ui.theme.GlowSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val angle: Float,
    val speed: Float,
    val size: Float
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<Particle>() }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            particles.clear()
            repeat(30) {
                particles.add(
                    Particle(
                        color = listOf(Accent1, Accent2, GlowPrimary, GlowSecondary, Color.White).random(),
                        startX = 0.5f, // Center X (relative)
                        startY = 0.5f, // Center Y (relative)
                        angle = Random.nextFloat() * 2 * PI.toFloat(),
                        speed = Random.nextFloat() * 800f + 200f,
                        size = Random.nextFloat() * 8f + 4f
                    )
                )
            }
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000, easing = LinearEasing)
            )
            particles.clear()
        }
    }

    if (particles.isNotEmpty()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            
            particles.forEach { particle ->
                val time = progress.value
                // Physics: x = v * t, y = v * t + 0.5 * g * t^2 (gravity)
                val distance = particle.speed * time
                val gravity = 1000f * time * time
                
                val x = cx + cos(particle.angle) * distance
                val y = cy + sin(particle.angle) * distance + gravity
                
                val alpha = (1f - time).coerceIn(0f, 1f)
                
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.size * (1f - time * 0.5f),
                    center = Offset(x, y)
                )
            }
        }
    }
}

package de.grunert.wasantwort.ui.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.grunert.wasantwort.ui.theme.Accent1
import de.grunert.wasantwort.ui.theme.GlassBorderColor
import de.grunert.wasantwort.ui.theme.GlassGradientDark
import de.grunert.wasantwort.ui.theme.GlassGradientLight
import de.grunert.wasantwort.ui.theme.GlassHighlight
import de.grunert.wasantwort.ui.theme.GlassSheen
import de.grunert.wasantwort.ui.theme.GlassDepth
import de.grunert.wasantwort.ui.theme.GlassLightRim
import de.grunert.wasantwort.ui.theme.GlassSurfaceBase
import de.grunert.wasantwort.ui.theme.GlassSurfacePressed
import de.grunert.wasantwort.ui.theme.TextPrimary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings

/**
 * Checks if blur is available on this device (API 31+)
 */
private fun isBlurAvailable(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

/**
 * GlassCard - Base glass surface component mit echtem Glass-Effekt
 * Features:
 * - Transparente Surface (alpha 0.08-0.14)
 * - Border mit Light Rim
 * - Gradient-Licht (von oben links heller, unten rechts dunkler)
 * - Shadow/Elevation
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.99f else 1f,
        animationSpec = tween(120),
        label = "glass_card_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.9f else 1f,
        animationSpec = tween(120),
        label = "glass_card_alpha"
    )
    
    val backgroundColor = if (isPressed) GlassSurfacePressed else GlassSurfaceBase
    val baseGradient = Brush.linearGradient(
        colors = listOf(
            GlassGradientLight,
            GlassGradientDark
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
    )
    val highlightBrush = Brush.linearGradient(
        colors = listOf(
            GlassHighlight,
            Color.Transparent,
            GlassDepth
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(900f, 900f)
    )
    val sheenBrush = Brush.radialGradient(
        colors = listOf(
            GlassSheen,
            Color.Transparent
        ),
        center = androidx.compose.ui.geometry.Offset(0f, 0f),
        radius = 500f
    )
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = Color.Black.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = GlassLightRim,
                shape = RoundedCornerShape(cornerRadius)
            )
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                        .scale(scale)
                        .alpha(alpha)
                } else {
                    Modifier
                }
            )
    ) {
        Box(modifier = Modifier.matchParentSize().background(baseGradient))
        Box(modifier = Modifier.matchParentSize().background(highlightBrush))
        Box(modifier = Modifier.matchParentSize().background(sheenBrush))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassLightRim.copy(alpha = 0.35f))
                .align(Alignment.TopCenter)
        )
        content()
    }
}

/**
 * GlassButton - Primary action button with glass styling
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    text: String,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = tween(100),
        label = "glass_button_scale"
    )
    
    val buttonAlpha = if (enabled) 1f else DisabledAlpha
    
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(48.dp)
            .scale(scale)
            .alpha(buttonAlpha),
        border = BorderStroke(1.dp, GlassLightRim.copy(alpha = if (enabled) 0.35f else 0.15f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent1.copy(alpha = 0.92f),
            contentColor = TextPrimary,
            disabledContainerColor = Accent1.copy(alpha = DisabledAlpha),
            disabledContentColor = TextPrimary.copy(alpha = DisabledAlpha)
        ),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        interactionSource = interactionSource
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp),
                color = TextPrimary,
                strokeWidth = 2.dp
            )
            Text(
                text = "Wird generiert...",
                style = MaterialTheme.typography.labelLarge
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * GlassChip - Selectable chip with improved glass styling
 * Selected: filled glass + Akzent-Rim
 * Unselected: nur leicht getönte Glass Surface (weniger Outline)
 */
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 12.dp  // Größerer Radius für moderneres Aussehen
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (selected) 1.02f else 1f,
        animationSpec = tween(200),
        label = "glass_chip_scale"
    )
    
    // Selected: filled glass mit Akzent
    // Unselected: nur leicht getönte Glass Surface
    val backgroundColor = if (selected) {
        Accent1.copy(alpha = 0.3f)
    } else {
        GlassSurfaceBase
    }
    
    val borderColor = if (selected) {
        Accent1
    } else {
        GlassLightRim.copy(alpha = 0.08f)
    }
    
    val textColor = if (selected) {
        TextPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        GlassHighlight.copy(alpha = if (selected) 0.12f else 0.08f),
                        backgroundColor.copy(alpha = backgroundColor.alpha * 0.75f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(200f, 200f)
                )
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(scale)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

/**
 * GlassTopAppBar - Top app bar with glass styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTopAppBar(
    title: String,
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            actions = {
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Historie",
                        tint = TextPrimary
                    )
                }
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Einstellungen",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlassSurfaceBase,
                titleContentColor = TextPrimary,
                actionIconContentColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassLightRim.copy(alpha = 0.28f))
                .align(Alignment.BottomCenter)
        )
    }
}

/**
 * GlassSurface - Simple glass surface without card wrapper
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val surfaceBrush = Brush.linearGradient(
        colors = listOf(
            GlassGradientLight,
            GlassGradientDark
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
    )
    val surfaceSheen = Brush.radialGradient(
        colors = listOf(
            GlassSheen,
            Color.Transparent
        ),
        center = androidx.compose.ui.geometry.Offset(0f, 0f),
        radius = 500f
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(GlassSurfaceBase)
            .border(
                width = 1.dp,
                color = GlassLightRim,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Box(modifier = Modifier.matchParentSize().background(surfaceBrush))
        Box(modifier = Modifier.matchParentSize().background(surfaceSheen))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassLightRim.copy(alpha = 0.3f))
                .align(Alignment.TopCenter)
        )
        content()
    }
}

// Constants
private val DisabledAlpha = 0.38f

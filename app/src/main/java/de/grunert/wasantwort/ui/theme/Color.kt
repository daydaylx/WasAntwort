package de.grunert.wasantwort.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy Material Colors (for compatibility)
val DarkPrimary = Color(0xFF6C5CE7)
val DarkPrimaryVariant = Color(0xFF6C5CE7)
val DarkSecondary = Color(0xFF00D4AA)
val DarkBackground = Color(0xFF0A0A0F)
val DarkSurface = Color(0xFF1A1A2E)
val DarkError = Color(0xFFFF6B6B)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkOnSecondary = Color(0xFF000000)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkOnError = Color(0xFF000000)

// Glass Design Tokens
val GlassBackground = Color(0xFF0A0A0F)
val GlassSurface = Color(0xFF1A1A2E)
val GlassBorder = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)
val Accent1 = Color(0xFF6C5CE7)
val Accent2 = Color(0xFF00D4AA)
val Danger = Color(0xFFFF6B6B)
val Success = Color(0xFF51CF66)

// Alpha/Opacity Values
val GlassSurfaceAlpha = 0.12f
val GlassSurfacePressedAlpha = 0.18f
val GlassBorderAlpha = 0.12f
val GlassOverlayAlpha = 0.08f
val TextSecondaryAlpha = 0.70f
val DisabledAlpha = 0.38f

// Glass Colors with Alpha
val GlassSurfaceBase = GlassSurface.copy(alpha = GlassSurfaceAlpha)
val GlassSurfacePressed = GlassSurface.copy(alpha = GlassSurfacePressedAlpha)
val GlassBorderColor = GlassBorder.copy(alpha = GlassBorderAlpha)
val GlassGradientStart = Color(0xFF1A1A2E).copy(alpha = GlassSurfaceAlpha)
val GlassGradientEnd = Color(0xFF2A2A3E).copy(alpha = GlassOverlayAlpha)




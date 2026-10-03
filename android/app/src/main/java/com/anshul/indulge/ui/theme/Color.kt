package com.anshul.indulge.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Indulge Brand Colors
val IndulgePurplePrimary = Color(0xFF6366F1) // Indigo/Purple accent from screenshots
val IndulgePurpleSecondary = Color(0xFF4F46E5)
val IndulgePurpleLight = Color(0xFFA5B4FC)
val IndulgePurpleDark = Color(0xFF312E81)

// Dark Theme Surfaces
val DarkBg = Color(0xFF070709) // Pure deep sleek black
val DarkSurface = Color(0xFF131318)
val DarkSurfaceVariant = Color(0xFF1C1C24)
val DarkCardBorder = Color(0xFF2A2A36)

// Text Colors
val TextWhitePrimary = Color(0xFFF8FAFC)
val TextGraySecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Screenshot Specific Colors
val AccordionActiveBg = Color(0xFF2D2967) // Purple active FAQ card from mockup
val AccordionActiveBorder = Color(0xFF4338CA)
val AccordionInactiveBg = Color(0xFF0E0E12) // Dark inactive FAQ pill
val AccordionInactiveBorder = Color(0xFF272732)

val VenueCardBg = Color(0xFFFFFFFF) // Crisp white venue card from mockup
val VenueTitleDark = Color(0xFF0F172A)
val VenueAddressDark = Color(0xFF64748B)
val VenuePurpleAction = Color(0xFF5856D6)

// Highlights & Banner Gradients (For placeholder without images, with beautiful colors)
val BannerGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2E1065),
        Color(0xFF1E1B4B),
        Color(0xFF0F172A)
    )
)

val HighlightGradient1 = Brush.linearGradient(
    colors = listOf(Color(0xFF312E81), Color(0xFF4C1D95), Color(0xFF1E1B4B))
)

val HighlightGradient2 = Brush.linearGradient(
    colors = listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF0F172A))
)

val HighlightGradient3 = Brush.linearGradient(
    colors = listOf(Color(0xFF831843), Color(0xFF701A75), Color(0xFF1E1B4B))
)

val HighlightGradient4 = Brush.linearGradient(
    colors = listOf(Color(0xFF1E3A8A), Color(0xFF1E1B4B), Color(0xFF0A0A0E))
)

// Legacy Material 3 tokens for backward compatibility
val Purple80 = IndulgePurpleLight
val PurpleGrey80 = Color(0xFFCBD5E1)
val Pink80 = Color(0xFFF472B6)

val Purple40 = IndulgePurplePrimary
val PurpleGrey40 = Color(0xFF475569)
val Pink40 = Color(0xFFDB2777)
package pl.edu.ur.teachly.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Base palette

// Deep Green (brand primary)
val DeepGreen950 = Color(0xFF0B1A0F)
val DeepGreen900 = Color(0xFF112A14)
val DeepGreen800 = Color(0xFF1E3A1E)
val DeepGreen700 = Color(0xFF2E7D32)
val DeepGreen600 = Color(0xFF388E3C)   // PRIMARY
val DeepGreen500 = Color(0xFF4CAF50)
val DeepGreen200 = Color(0xFFA5D6A7)
val DeepGreen50  = Color(0xFFE8F5E9)

// Mint Green (secondary)
val Mint700 = Color(0xFF66BB6A)
val Mint600 = Color(0xFF81C784)
val Mint500 = Color(0xFFA5D6A7)
val Mint200 = Color(0xFFC8E6C9)
val Mint50  = Color(0xFFE8F5E9)

// Light Green Accent (highlight / CTA)
val LightGreen600 = Color(0xFFB2FF59)
val LightGreen500 = Color(0xFFCCFF90)
val LightGreen200 = Color(0xFFE6FFCC)
val LightGreen50  = Color(0xFFF2FFE6)

// Neutral
val Neutral900 = Color(0xFF0D1F10)
val Neutral700 = Color(0xFF2F3F2E)
val Neutral500 = Color(0xFF64746B)
val Neutral300 = Color(0xFFB0BFB2)
val Neutral200 = Color(0xFFD9E2D9)
val Neutral100 = Color(0xFFF1F8F1)
val Neutral50  = Color(0xFFF8FBF8)

// Functional
val ErrorRed = Color(0xFFEF4444)
val ErrorContainerRed = Color(0xFF5B1C1C)
val SuccessGreen = Color(0xFF22C55E)
val GoogleBlue = Color(0xFF4285F4)

val AvatarColors: List<Pair<Color, Color>> = listOf(
    Color(0xFFDBEAFE) to Color(0xFF1D4ED8),
    Color(0xFFDCFCE7) to Color(0xFF15803D),
    Color(0xFFFCE7F3) to Color(0xFF9D174D),
    Color(0xFFFEF3C7) to Color(0xFF92400E),
    Color(0xFFF3E8FF) to Color(0xFF6B21A8),
    Color(0xFFFFEDD5) to Color(0xFF9A3412),
)


// Light color scheme
val LightColorScheme = lightColorScheme(

    primary = DeepGreen600,
    onPrimary = Color.White,
    primaryContainer = DeepGreen50,
    onPrimaryContainer = DeepGreen900,

    secondary = Mint600,
    onSecondary = Color.White,
    secondaryContainer = Mint50,
    onSecondaryContainer = Mint700,

    tertiary = LightGreen500,
    onTertiary = Color.White,
    tertiaryContainer = LightGreen50,
    onTertiaryContainer = LightGreen600,

    background = Neutral50,
    onBackground = Neutral900,

    surface = Color.White,
    onSurface = Neutral900,

    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral500,

    outline = Neutral200,
    outlineVariant = Neutral100,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = LightGreen50,
    onErrorContainer = LightGreen600,

    inverseSurface = Neutral900,
    inverseOnSurface = Neutral50,
    inversePrimary = DeepGreen200
)


// Dark color scheme
val DarkColorScheme = darkColorScheme(

    primary = DeepGreen600,
    onPrimary = Color.White,
    primaryContainer = DeepGreen800,
    onPrimaryContainer = DeepGreen900,

    secondary = Mint700,
    onSecondary = Color.White,
    secondaryContainer = Mint700,
    onSecondaryContainer = Mint200,

    tertiary = LightGreen500,
    onTertiary = Color.White,
    tertiaryContainer = LightGreen600,
    onTertiaryContainer = LightGreen200,

    background = DeepGreen950,
    onBackground = Neutral100,

    surface = Neutral900,
    onSurface = Neutral100,

    surfaceVariant = Neutral700,
    onSurfaceVariant = Neutral300,

    outline = Neutral700,
    outlineVariant = Neutral900,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainerRed,
    onErrorContainer = LightGreen200,

    inverseSurface = Neutral50,
    inverseOnSurface = Neutral900,
    inversePrimary = DeepGreen600
)
package dev.ayupi.pim.core.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

@Composable
fun rememberStorageColors(seed: String): Pair<Color, Color> {
    val isDark = isSystemInDarkTheme()
    return remember(seed, isDark) {
        val hash = seed.hashCode()

        val hue = abs(hash % 360f)
        
        if (isDark) {
            val saturation = 0.25f
            val lightness = 0.15f
            val backgroundColor = Color.hsl(hue, saturation, lightness)

            val contentSaturation = 0.6f
            val contentLightness = 0.85f
            val contentColor = Color.hsl(hue, contentSaturation, contentLightness)

            backgroundColor to contentColor
        } else {
            val saturation = 0.4f
            val lightness = 0.92f
            val backgroundColor = Color.hsl(hue, saturation, lightness)

            val contentSaturation = 0.8f
            val contentLightness = 0.25f
            val contentColor = Color.hsl(hue, contentSaturation, contentLightness)

            backgroundColor to contentColor
        }
    }
}
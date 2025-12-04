package dev.ayupi.pse_new.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ayupi.pse_new.core.ui.util.rememberStorageColors

@Composable
fun StorageChip(
    label: String,
    isSelected: Boolean, // <--- Wichtig für Filter!
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (baseBg, baseContent) = rememberStorageColors(label)

    val backgroundColor = if (isSelected) baseBg else Color.Transparent
    val contentColor = if (isSelected) baseContent else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) baseBg else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .height(32.dp)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
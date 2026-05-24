package dev.ayupi.pim.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SpeedDialItem(
    val icon: ImageVector,
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val onClick: () -> Unit
)

@Composable
fun SpeedDialFab(
    isExpanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    items: List<SpeedDialItem>,
    modifier: Modifier = Modifier,
    mainIcon: ImageVector = Icons.Default.Add
) {
    val transition = updateTransition(targetState = isExpanded, label = "SpeedDialTransition")

    val rotation by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = 250, easing = FastOutSlowInEasing)
            }
        },
        label = "rotation"
    ) { expanded ->
        if (expanded) 135f else 0f
    }

    val progress by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
            } else {
                tween(durationMillis = 200, easing = FastOutSlowInEasing)
            }
        },
        label = "progress"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // FAB and actions stack
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = if (isExpanded) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.wrapContentSize()
            }
        ) {
            if (progress > 0f) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEachIndexed { index, item ->
                        val itemIndex = items.size - 1 - index
                        val startFraction = itemIndex * 0.15f
                        val itemProgress = if (progress >= startFraction) {
                            if (startFraction >= 1f) 1f else ((progress - startFraction) / (1f - startFraction)).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                        
                        if (itemProgress > 0f) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        alpha = itemProgress
                                        scaleX = 0.6f + (0.4f * itemProgress)
                                        scaleY = 0.6f + (0.4f * itemProgress)
                                        translationY = (1f - itemProgress) * 50.dp.toPx()
                                    }
                            ) {
                                // Label Card (Capsule Pill with Outline)
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            item.onClick()
                                            onExpandChanged(false)
                                        }
                                ) {
                                    Text(
                                        text = item.label,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Action Button
                                SmallFloatingActionButton(
                                    onClick = {
                                        item.onClick()
                                        onExpandChanged(false)
                                    },
                                    containerColor = item.containerColor,
                                    contentColor = item.contentColor
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main FAB toggling menu expansion
            FloatingActionButton(
                onClick = {
                    onExpandChanged(!isExpanded)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = mainIcon,
                    contentDescription = if (isExpanded) "Menü schließen" else "Menü öffnen",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

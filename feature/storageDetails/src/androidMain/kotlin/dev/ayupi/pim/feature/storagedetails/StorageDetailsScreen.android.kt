package dev.ayupi.pim.feature.storagedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun ListScrollbar(
    modifier: Modifier,
    state: LazyListState
) {
}

@Composable
actual fun AlphabetFilterBar(
    itemFilter: ItemFilter,
    onToggleFilter: (filter: ItemFilter) -> Unit,
    availableLetters: List<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Abstand zwischen Buchstaben
    ) {
        item {
            IconButton(onClick = {
                when (itemFilter) {
                    ItemFilter.ALL -> onToggleFilter(ItemFilter.EXPIRED_SOON)
                    ItemFilter.EXPIRED_SOON -> onToggleFilter(ItemFilter.ALL)
                }
            }) {
                Icon(
                    if (itemFilter == ItemFilter.ALL) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                    contentDescription = "Filter"
                )
            }
        }
        item {
                IconButton(enabled = false, onClick = {}) {
                Icon(
                    imageVector = Icons.Default.SortByAlpha,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                }
        }
        items(availableLetters.toList()) { letter ->
            IconButton(onClick = { onLetterClick(letter) }) {
                Text(text = letter.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
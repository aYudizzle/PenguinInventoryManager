package dev.ayupi.pim.feature.storagedetails

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun ListScrollbar(
    modifier: Modifier,
    state: LazyListState
) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(state),
        modifier = modifier
    )
}

@Composable
actual fun AlphabetFilterBar(
    itemFilter: ItemFilter,
    onToggleFilter: (filter: ItemFilter) -> Unit,
    availableLetters: List<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp).height(48.dp)
        ) {
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
            Icon(
                imageVector = Icons.Default.SortByAlpha,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Springe zu:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        availableLetters.forEach { letter ->
            IconButton(onClick = { onLetterClick(letter) }) {
                Text(text = letter.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
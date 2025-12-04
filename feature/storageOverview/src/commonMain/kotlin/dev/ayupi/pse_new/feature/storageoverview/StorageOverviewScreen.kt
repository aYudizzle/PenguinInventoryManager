package dev.ayupi.pse_new.feature.storageoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ayupi.pse_new.core.model.StorageSummary
import dev.ayupi.pse_new.core.ui.util.rememberStorageColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StorageOverviewScreen(
    modifier: Modifier = Modifier,
    onStorageSelected: (String) -> Unit,
){
    val viewModel: StorageOverviewViewModel = koinViewModel<StorageOverviewViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StorageOverviewContent(
        modifier = modifier,
        state = uiState,
        onStorageSelected = onStorageSelected
    )
}

@Composable
fun StorageOverviewContent(
    modifier: Modifier = Modifier,
    state: StorageOverviewUiState,
    onStorageSelected: (String) -> Unit
) {
    when(state){
        is StorageOverviewUiState.Loading -> Text("Loading...")
        StorageOverviewUiState.Empty -> {}
        is StorageOverviewUiState.Success -> {
            var lastClickTime by remember { mutableLongStateOf(0L) }

            LazyColumn(
                modifier = modifier,
            ) {
                items(state.storages, key = { it.id }) {
                    StorageCard(
                        storage = it,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if(now - lastClickTime > 1000) { // 0.5s kann zu kurz sein und einen Doppelklick durchlassen
                                lastClickTime = now
                                onStorageSelected(it.id)
                            }
                        },
                    )
                }
            }
        }
    }

}


@Composable
fun StorageCard(
    storage: StorageSummary,
    onClick: () -> Unit
) {
    val (iconBgColor, iconTintColor) = rememberStorageColors(storage.name)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)) // Runde Ecken wie im Design
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface, // Weiß/Dunkel
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Dezent, fast flach
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2, // Box-Icon
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = storage.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val productText = if (storage.productCount == 1) "1 Produkt" else "${storage.productCount} Produkte"

                Text(
                    text = productText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Etwas grauer
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
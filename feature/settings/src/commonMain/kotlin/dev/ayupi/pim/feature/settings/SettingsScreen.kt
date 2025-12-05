package dev.ayupi.pim.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToItemMaster: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val userData by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        modifier = modifier,
        state = userData,
        onWarningDaysChange = viewModel::onWarningDaysChange,
        onNavigateToItemMaster = onNavigateToItemMaster,
    )
}

@Composable
fun SettingsContent(
    modifier: Modifier,
    state: SettingsUiState,
    onWarningDaysChange: (String) -> Unit,
    onNavigateToItemMaster: () -> Unit = {}
) {

    when(state) {
        SettingsUiState.Loading -> {}
        is SettingsUiState.Success -> {
            Column(modifier = modifier.padding(16.dp)) {
                Text("Ablaufdatum Warnung", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Wie viele Tage vorher soll gewarnt werden?",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.expirationDaysInput,
                    onValueChange = { onWarningDaysChange(it) },
                    label = { Text("Tage") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Sektion: Datenverwaltung
                Text("Datenverwaltung", style = MaterialTheme.typography.titleMedium)

                ListItem(
                    headlineContent = { Text("Alle Produkte bearbeiten") },
                    supportingContent = { Text("Namen korrigieren oder aufräumen") },
                    leadingContent = { Icon(Icons.Default.Edit, null) },
                    trailingContent = { Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, null) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToItemMaster() }
                )
            }
        }
    }

}
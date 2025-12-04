package dev.ayupi.pse_new.core.ui.model

data class DisplayableQuantity(
    val rawQuantity: Long,
    val rawSize: Int,
    val rawUnit: String,
    // formatted values fuer UI
    val label: String,
    val totalLabel: String?,
)

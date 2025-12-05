package dev.ayupi.pim.core.ui.model

data class StorageItemUi(
    val id: String,
    val itemName: String,
    val storageName: String,
    val quantity: DisplayableQuantity,

    val expirationDate: DisplayableDate?,
    val expirationStatus: ExpirationStatus,
    val lastUpdated: DisplayableInstant,
)
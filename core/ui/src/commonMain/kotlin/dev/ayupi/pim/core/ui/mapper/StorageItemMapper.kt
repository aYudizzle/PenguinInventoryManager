package dev.ayupi.pim.core.ui.mapper

import dev.ayupi.pim.core.model.StorageItem
import dev.ayupi.pim.core.ui.model.StorageItemUi

fun StorageItem.toUiModel(
    warningThreshold: Int,
) =
    StorageItemUi(
        id = this.id,
        itemName = this.item.name,
        storageName = this.storage.name,
        quantity = mapQuantityToDisplayableModel(quantity, unit, itemSize),
        expirationDate = expirationDate?.toDisplayableDate(),
        expirationStatus = expirationDate.toExpirationStatus(warningThreshold),
        lastUpdated = this.updatedAt.toDisplayableInstant(),
    )
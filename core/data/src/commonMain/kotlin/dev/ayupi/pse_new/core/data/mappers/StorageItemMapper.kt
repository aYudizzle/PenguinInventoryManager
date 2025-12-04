package dev.ayupi.pse_new.core.data.mappers

import dev.ayupi.pse_new.core.database.entity.StorageItemWithDetails
import dev.ayupi.pse_new.core.model.Item
import dev.ayupi.pse_new.core.model.Storage
import dev.ayupi.pse_new.core.model.StorageItem
import dev.ayupi.pse_new.core.model.StorageUnit
import dev.ayupi.pse_new.core.network.model.NestedItem
import dev.ayupi.pse_new.core.network.model.NestedQuantity
import dev.ayupi.pse_new.core.network.model.NestedStorage
import dev.ayupi.pse_new.core.network.model.StorageItemDto

fun StorageItemWithDetails.toDto(): StorageItemDto {
    return StorageItemDto(
        id = this.entry.id.toString(),

        item = NestedItem(
            id = this.item.id.toString(),
            name = this.item.name
        ),

        storage = NestedStorage(
            id = this.storage.id.toString(),
            name = this.storage.name
        ),

        quantityInfo = NestedQuantity(
            count = this.entry.quantity,
            unit = this.entry.unit,
            sizePerUnit = this.entry.itemSize,
        ),

        expirationDate = this.entry.expirationDate,

        createdAt = this.entry.createdAt,
        updatedAt = this.entry.updatedAt,

        deletedAt = if (this.entry.isDeleted) this.entry.deletedAt else null
    )
}


fun StorageItemWithDetails.toDomain(): StorageItem {
    return StorageItem(
        id = this.entry.id.toString(),

        item = Item(
            id = this.item.id.toString(),
            name = this.item.name,
            updatedAt = this.item.updatedAt,
            createdAt = this.item.createdAt,
        ),

        storage = Storage(
            id = this.storage.id.toString(),
            name = this.storage.name,
            updatedAt = this.storage.updatedAt,
            createdAt = this.storage.createdAt,
        ),
        itemSize = this.entry.itemSize,
        quantity = this.entry.quantity,
        unit = StorageUnit.fromString(this.entry.unit),
        expirationDate = this.entry.expirationDate,

        // Domain-spezifische Zeitstempel
        updatedAt = this.entry.updatedAt,
        createdAt = this.entry.createdAt,
    )
}
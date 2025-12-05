package dev.ayupi.pim.core.data.mappers

import dev.ayupi.pim.core.database.entity.ItemEntity
import dev.ayupi.pim.core.model.Item
import dev.ayupi.pim.core.network.model.ItemDto

fun ItemEntity.toDto() =
    ItemDto(
        id = id.toString(),
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

fun ItemEntity.toDomain() = Item(
    id = id.toString(),
    name = name,
    updatedAt = updatedAt,
    createdAt = createdAt,
)

fun List<ItemEntity>.toDomain() = map { it.toDomain() }
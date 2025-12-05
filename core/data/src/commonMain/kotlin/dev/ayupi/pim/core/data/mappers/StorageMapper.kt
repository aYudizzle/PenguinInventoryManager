package dev.ayupi.pim.core.data.mappers

import dev.ayupi.pim.core.database.entity.StorageEntity
import dev.ayupi.pim.core.database.entity.StorageWithCount
import dev.ayupi.pim.core.model.Storage
import dev.ayupi.pim.core.model.StorageSummary
import dev.ayupi.pim.core.network.model.StorageDto

fun StorageEntity.toDto() =
    StorageDto(
        id = id.toString(),
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

fun StorageEntity.toDomain() = Storage(
    id = id.toString(),
    name = name,
    updatedAt = updatedAt,
    createdAt = createdAt,
)

fun List<StorageEntity>.toDomain() = map { it.toDomain() }

fun StorageWithCount.toDomain() = StorageSummary(
    id = storage.id.toString(),
    name = storage.name,
    productCount = itemCount,
    updatedAt = storage.updatedAt,
)
fun List<StorageWithCount>.toDomainList() = map { it.toDomain() }
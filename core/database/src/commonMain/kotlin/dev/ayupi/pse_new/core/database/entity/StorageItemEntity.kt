package dev.ayupi.pse_new.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "storage_items", foreignKeys = [ForeignKey(
        entity = StorageEntity::class,
        parentColumns = ["id"],
        childColumns = ["storageId"],
        onDelete = ForeignKey.CASCADE,
    ), ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("storageId"), Index("itemId")]
)
data class StorageItemEntity(
    @PrimaryKey(autoGenerate = false) val id: Uuid = Uuid.random(),
    val storageId: Uuid,
    val itemId: Uuid,
    val quantity: Long,
    val itemSize: Int,
    val unit: String,
    @SerialName("expiration_date")
    val expirationDate: LocalDate?,

    // # Sync Fields
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val isDeleted: Boolean = false,
    val isDirty: Boolean = false
    )

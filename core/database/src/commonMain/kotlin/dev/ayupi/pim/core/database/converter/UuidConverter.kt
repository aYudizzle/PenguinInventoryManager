package dev.ayupi.pim.core.database.converter

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidConverter {
    @TypeConverter
    fun fromUuid(uuid: Uuid?): String? = uuid?.toString()

    @TypeConverter
    fun toUuid(value: String?): Uuid? = value?.let { Uuid.parse(it) }
}
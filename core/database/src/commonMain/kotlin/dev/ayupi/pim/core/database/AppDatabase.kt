package dev.ayupi.pim.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import dev.ayupi.pim.core.database.converter.InstantConverter
import dev.ayupi.pim.core.database.converter.LocalDateConverter
import dev.ayupi.pim.core.database.converter.UuidConverter
import dev.ayupi.pim.core.database.dao.InventoryDao
import dev.ayupi.pim.core.database.dao.ItemDao
import dev.ayupi.pim.core.database.dao.StorageDao
import dev.ayupi.pim.core.database.entity.ItemEntity
import dev.ayupi.pim.core.database.entity.StorageEntity
import dev.ayupi.pim.core.database.entity.StorageItemEntity

@Database(
    entities = [
        ItemEntity::class,
        StorageEntity::class,
        StorageItemEntity::class
    ],
    version = 2,
)
@TypeConverters(
    UuidConverter::class,
    LocalDateConverter::class,
    InstantConverter::class
)

@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val storageDao: StorageDao
    abstract val inventoryDao: InventoryDao
    abstract val itemDao: ItemDao
}


expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
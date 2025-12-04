package dev.ayupi.pse_new.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import dev.ayupi.pse_new.core.database.converter.InstantConverter
import dev.ayupi.pse_new.core.database.converter.LocalDateConverter
import dev.ayupi.pse_new.core.database.converter.UuidConverter
import dev.ayupi.pse_new.core.database.dao.InventoryDao
import dev.ayupi.pse_new.core.database.dao.ItemDao
import dev.ayupi.pse_new.core.database.dao.StorageDao
import dev.ayupi.pse_new.core.database.entity.ItemEntity
import dev.ayupi.pse_new.core.database.entity.StorageEntity
import dev.ayupi.pse_new.core.database.entity.StorageItemEntity

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
// WICHTIG FÜR KMP: Verknüpft die DB mit dem generierten Constructor
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val storageDao: StorageDao
    abstract val inventoryDao: InventoryDao
    abstract val itemDao: ItemDao
}

// Die "Erwartung": Jede Plattform muss diesen Constructor bereitstellen
// (Der Code dafür wird aber automatisch von Room generiert, wir müssen ihn nur in actual verknüpfen)
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
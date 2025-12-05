package dev.ayupi.pim.core.database

import androidx.room.RoomDatabase

interface DatabaseFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}
package dev.ayupi.pse_new.core.database

import androidx.room.RoomDatabase

interface DatabaseFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}
package dev.ayupi.pim.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
    override fun create(): RoomDatabase.Builder<AppDatabase> {
        val db = context.getDatabasePath("pse.db")
        return Room.databaseBuilder<AppDatabase>(context = context, name = db.path)
    }
}

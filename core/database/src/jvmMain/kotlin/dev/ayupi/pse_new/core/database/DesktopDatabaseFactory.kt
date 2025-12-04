package dev.ayupi.pse_new.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import java.io.File

class DesktopDatabaseFactory : DatabaseFactory {
    override fun create(): RoomDatabase.Builder<AppDatabase> {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".pse_app")
        if(!appDir.exists()) appDir.mkdirs()

        val dbFile = File(appDir, "storage_app.db")

        dbFile.parentFile?.mkdirs()

        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath
        )
    }
}

package dev.ayupi.pse_new.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import dev.ayupi.pse_new.core.datastore.model.UserData
import dev.ayupi.pse_new.core.datastore.serializer.AppSettingsSerializer
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

object AppSettingsFactory {
    fun create(producePath: () -> String): DataStore<UserData> =
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = AppSettingsSerializer,
                producePath = { producePath().toPath() }
            )
        )
}
package dev.ayupi.pim.core.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import dev.ayupi.pim.core.datastore.model.UserData
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.use

object AppSettingsSerializer : OkioSerializer<UserData> {
    override val defaultValue: UserData = UserData()

    override suspend fun readFrom(source: BufferedSource): UserData =
        try {
            Json.decodeFromString(
                deserializer = UserData.serializer(),
                string = source.readUtf8()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }

    override suspend fun writeTo(t: UserData, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(Json.encodeToString(UserData.serializer(), t))
        }
    }
}
package dev.ayupi.pim.core.network

import dev.ayupi.pim.core.network.model.ApiResponse
import dev.ayupi.pim.core.network.model.ItemDto
import dev.ayupi.pim.core.network.model.StorageDto
import dev.ayupi.pim.core.network.model.StorageItemDto
import dev.ayupi.pim.core.network.model.SyncInventoryRequest
import dev.ayupi.pim.core.network.model.SyncItemsRequest
import dev.ayupi.pim.core.network.model.SyncStoragesRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class StorageApi(private val client: HttpClient) {

    // ========================================================================
    // 1. ITEMS (Master Data)
    // ========================================================================

    suspend fun getItems(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<ItemDto>> {
        return client.get("api/items") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }


    suspend fun syncItemsBatch(
        request: SyncItemsRequest
    ): ApiResponse<List<ItemDto>> {
        return client.post("api/items/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ========================================================================
    // 2. STORAGES (Master Data)
    // ========================================================================


    suspend fun getStorages(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<StorageDto>> {
        return client.get("api/storages") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }

    suspend fun syncStoragesBatch(
        request: SyncStoragesRequest
    ): ApiResponse<List<StorageDto>> {
        return client.post("api/storages/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ========================================================================
    // 3. INVENTORY (Pivot Data)
    // ========================================================================


    suspend fun getInventory(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<StorageItemDto>> {
        return client.get("api/inventory") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }

    suspend fun syncInventoryBatch(
        request: SyncInventoryRequest
    ): ApiResponse<List<StorageItemDto>> {
        return client.post("api/inventory/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
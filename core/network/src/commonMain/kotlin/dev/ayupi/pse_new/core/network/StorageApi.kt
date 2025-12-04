package dev.ayupi.pse_new.core.network

import dev.ayupi.pse_new.core.network.model.ApiResponse
import dev.ayupi.pse_new.core.network.model.ItemDto
import dev.ayupi.pse_new.core.network.model.StorageDto
import dev.ayupi.pse_new.core.network.model.StorageItemDto
import dev.ayupi.pse_new.core.network.model.SyncInventoryRequest
import dev.ayupi.pse_new.core.network.model.SyncItemsRequest
import dev.ayupi.pse_new.core.network.model.SyncStoragesRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class StorageApi(private val client: HttpClient) {

    // ========================================================================
    // 1. ITEMS (Master Data)
    // ========================================================================

    /**
     * PULL: GET api/items
     */
    suspend fun getItems(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<ItemDto>> {
        return client.get("api/items") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }

    /**
     * PUSH: POST api/items/sync
     */
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

    /**
     * PULL: GET api/storages
     */
    suspend fun getStorages(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<StorageDto>> {
        return client.get("api/storages") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }

    /**
     * PUSH: POST api/storages/sync
     */
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

    /**
     * PULL: GET api/inventory
     */
    suspend fun getInventory(
        since: String? = null,
        cursor: String? = null
    ): ApiResponse<List<StorageItemDto>> {
        return client.get("api/inventory") {
            if (since != null) parameter("since", since)
            if (cursor != null) parameter("cursor", cursor)
        }.body()
    }

    /**
     * PUSH: POST api/inventory/sync
     */
    suspend fun syncInventoryBatch(
        request: SyncInventoryRequest
    ): ApiResponse<List<StorageItemDto>> {
        return client.post("api/inventory/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
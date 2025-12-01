package com.green_solar.gs_app.data.repository

import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.dto.CartCreateRequest
import com.green_solar.gs_app.data.remote.dto.CartUpdateRequest
import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.repository.CartRepository

class CartRepositoryImpl(
    private val api: ApiService,
    private val session: SessionManager
) : CartRepository {

    override suspend fun getUserCarts(): Result<List<Cart>> = runCatching {
        val token = session.getToken() ?: throw Exception("User not authenticated")
        api.getUserCarts("Bearer $token").map { it.toDomain() }
    }

    override suspend fun getCart(cartId: Long): Result<Cart> = runCatching {
        val token = session.getToken() ?: throw Exception("User not authenticated")
        api.getCart("Bearer $token", cartId).toDomain()
    }

    /**
     * Creates a cart by sending only the product IDs, as required by the backend DTO.
     * The quantity information from the UI is disregarded at creation time.
     */
    override suspend fun createCart(name: String, description: String?, items: Map<Long, Int>): Result<Cart> = runCatching {
        if (items.isEmpty()) {
            throw IllegalArgumentException("Cannot create a cart with no products.")
        }
        val token = session.getToken() ?: throw Exception("User not authenticated")

        // Extract only the product IDs (keys from the map) to match the backend DTO.
        val productIds = items.keys.toList()

        val request = CartCreateRequest(name = name, description = description, productIds = productIds)
        api.createCart("Bearer $token", request).toDomain()
    }

    override suspend fun updateCart(cartId: Long, name: String?, description: String?, productIds: List<Long>?): Result<Cart> = runCatching {
        val token = session.getToken() ?: throw Exception("User not authenticated")
        val request = CartUpdateRequest(name, description, productIds ?: emptyList())
        api.updateCart("Bearer $token", cartId, request).toDomain()
    }

    override suspend fun deleteCart(cartId: Long): Result<Unit> = runCatching {
        val token = session.getToken() ?: throw Exception("User not authenticated")
        api.deleteCart("Bearer $token", cartId)
    }
}

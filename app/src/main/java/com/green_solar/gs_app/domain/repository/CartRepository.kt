package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Cart

/**
 * Interface for the Cart repository.
 */
interface CartRepository {

    /**
     * Fetches all carts for the current user.
     */
    suspend fun getUserCarts(): Result<List<Cart>>

    /**
     * Fetches a single cart by its ID.
     */
    suspend fun getCart(cartId: Long): Result<Cart>

    /**
     * Creates a new cart.
     * CORRECTED: The method now accepts a map of product IDs to their quantities,
     * instead of just a list of IDs.
     * @param name The name of the cart.
     * @param description A description for the cart.
     * @param items A map where the key is the product ID and the value is the quantity.
     */
    suspend fun createCart(name: String, description: String?, items: Map<Long, Int>): Result<Cart>

    /**
     * Updates an existing cart.
     */
    suspend fun updateCart(cartId: Long, name: String?, description: String?, productIds: List<Long>?): Result<Cart>

    /**
     * Deletes a cart by its ID.
     */
    suspend fun deleteCart(cartId: Long): Result<Unit>
}

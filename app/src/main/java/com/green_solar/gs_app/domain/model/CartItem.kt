package com.green_solar.gs_app.domain.model

/**
 * Domain model for an item within a shopping cart.
 * It links a Product with a specific quantity.
 */
data class CartItem(
    val id: Long,
    val product: Product,
    val quantity: Int
)

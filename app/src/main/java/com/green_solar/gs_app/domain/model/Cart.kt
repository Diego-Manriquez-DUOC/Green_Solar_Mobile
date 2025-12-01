package com.green_solar.gs_app.domain.model

/**
 * Domain model for a shopping Cart.
 * It now contains a list of CartItems, which link products with quantities.
 */
data class Cart(
    val id: String,
    val name: String,
    val description: String?,
    val cartItems: List<CartItem> = emptyList()
)

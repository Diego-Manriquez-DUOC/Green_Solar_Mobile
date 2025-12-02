package com.green_solar.gs_app.ui.components.cart

import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.model.Product

/**
 * Represents the UI state for the Cart screen.
 */
data class CartState(
    // For the list of available products
    val isLoadingProducts: Boolean = false,
    val products: List<Product> = emptyList(),
    val productsError: String? = null,

    // For the list of user's existing carts
    val isLoadingCarts: Boolean = false,
    val carts: List<Cart> = emptyList(),
    val cartsError: String? = null,

    // For the cart creation process
    val isCreating: Boolean = false,
    val creationSuccess: Boolean = false,
    val creationError: String? = null,
    val newlyCreatedCart: Cart? = null,

    // For the cart deletion process
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null,

    // For the cart update process
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null,
    val selectedCart: Cart? = null
)

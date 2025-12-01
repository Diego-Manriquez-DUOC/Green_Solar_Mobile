package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Requests --- //

data class CartCreateRequest(
    val name: String,
    val description: String?,
    val productIds: List<Long> // Assuming this is still used for creation
)

data class CartUpdateRequest(
    val name: String?,
    val description: String?,
    val productIds: List<Long>? // Assuming this is still used for update
)


// --- Responses --- //

/**
 * DTO for a single item within a cart, including the product and quantity.
 * Based on the backend entity.
 */
data class CartItemResponseDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("product")
    val product: ProductResponseDTO
)

/**
 * DTO for the full Cart response from the API.
 * Now contains a list of CartItem DTOs.
 */
data class CartResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("cartItems")
    val cartItems: List<CartItemResponseDTO>
)

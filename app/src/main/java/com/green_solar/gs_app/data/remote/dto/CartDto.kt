package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Requests --- //

/**
 * Represents a single item to be included when creating a cart.
 */
data class CartItemCreateRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("quantity") val quantity: Int
)

/**
 * DTO for creating a new Cart.
 * CORRECTED: The property name in the class MUST match the one used in the repository.
 */
data class CartCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    // The JSON field is 'cartItems', and now the Kotlin property is also 'cartItems'.
    @SerializedName("cartItems") val cartItems: List<CartItemCreateRequest> = emptyList()
)


data class CartUpdateRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("productIds") val productIds: List<Long>?
)


// --- Responses --- //

data class CartItemResponseDTO(
    @SerializedName("id") val id: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: ProductResponseDTO
)

/**
 * DTO for the full Cart response from the API.
 */
data class CartResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("cartItems") val cartItems: List<CartItemResponseDTO>?
)

package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.green_solar.gs_app.domain.model.ProductCategory

// --- Requests -- //

data class CartItemRequest(
    @SerializedName("productId") val productId: Long,
    @SerializedName("quantity") val quantity: Int
)

data class CartCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("items") val items: List<CartItemRequest>
)

data class CartUpdateRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("productIds") val productIds: List<Long>?
)


// --- Responses --- //

/**
 * DTO for a single item in a cart response.
 * Verified to match the backend's CartItemResponseDTO record.
 */
data class CartItemResponseDTO(
    @SerializedName("id") val id: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: ProductResponseDTO
)

/**
 * DTO for the full Cart response from the API.
 * Verified to match the backend's CartResponseDTO record.
 */
data class CartResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("userId") val userId: Long, // Note: Backend is userId, mapping it here.
    @SerializedName("items") val items: List<CartItemResponseDTO>?
)

package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.CartItemResponseDTO
import com.green_solar.gs_app.data.remote.dto.CartResponse
import com.green_solar.gs_app.data.remote.dto.ProductResponseDTO
import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.model.CartItem
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.model.User

/**
 * Maps a UserDto to a User domain model.
 * CORRECTED: Uses the new camelCase properties (userId, imgUrl) from the DTO.
 */
fun UserDto.toDomain(): User {
    return User(
        user_id = this.user_id,
        username = this.name,
        email = this.email,
        role = this.role,
        imgUrl = this.imgUrl
    )
}

/**
 * Maps a ProductResponseDTO (from API) to a Product domain model (clean).
 */
fun ProductResponseDTO.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        desc = this.desc,
        price = this.price,
        category = this.category,
        storageKW = this.storageKW,
        productionKW = this.productionKW,
        imgUrl = this.imgUrl
    )
}

/**
 * Maps a CartItemResponseDTO (from API) to a CartItem domain model (clean).
 */
fun CartItemResponseDTO.toDomain(): CartItem {
    return CartItem(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toDomain() // Reuse the existing product mapper
    )
}

/**
 * Maps a CartResponse (from API) to a Cart domain model (clean).
 */
fun CartResponse.toDomain(): Cart {
    return Cart(
        id = this.id.toString(),
        name = this.name,
        description = this.description,
        // Safely map the items, handling potential null list from API
        cartItems = this.cartItems?.map { it.toDomain() } ?: emptyList()
    )
}

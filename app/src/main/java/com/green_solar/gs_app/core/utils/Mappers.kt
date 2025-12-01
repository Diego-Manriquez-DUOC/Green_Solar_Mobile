package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.CartItemResponseDTO
import com.green_solar.gs_app.data.remote.dto.CartResponse
import com.green_solar.gs_app.data.remote.dto.ProductResponseDTO
import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.model.CartItem
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        user_id = this.user_id,
        name = this.name,
        email = this.email,
        role = this.role,
        img_url = this.img_url
    )
}

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
 * It now internally uses the CartItem mapper to convert the list.
 */
fun CartResponse.toDomain(): Cart {
    return Cart(
        id = this.id.toString(), // Converting Long to String for the domain model
        name = this.name,
        description = this.description,
        cartItems = this.cartItems.map { it.toDomain() } // Corrected: use cartItems
    )
}

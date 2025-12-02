package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.model.ProductCategory

interface CartRepository {
    suspend fun getUserCarts(): Result<List<Cart>>
    suspend fun getCart(cartId: Long): Result<Cart>
    suspend fun createCart(name: String, description: String?, items: Map<Long, Int>): Result<Cart>
    suspend fun updateCart(cartId: Long, name: String?, description: String?, productIds: List<Long>?): Result<Cart>
    suspend fun deleteCart(cartId: Long): Result<Unit>
    suspend fun searchProducts(name: String?, category: ProductCategory?): Result<List<Product>>
}

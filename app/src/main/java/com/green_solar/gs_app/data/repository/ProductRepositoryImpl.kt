package com.green_solar.gs_app.data.repository

import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.dto.toDomain
import com.green_solar.gs_app.data.remote.dto.toDto
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val api: ApiService,
    private val sessionManager: SessionManager
) : ProductRepository {

    override suspend fun GetProducts(): List<Product> {
        val token = sessionManager.getToken()
        return api.getProducts("Bearer $token").map { it.toDomain() }
    }

    override suspend fun CreateProduct(product: Product): Product {
        val token = sessionManager.getToken()
        val productDto = product.toDto()
        return api.createProduct("Bearer $token", productDto).toDomain()
    }

    override suspend fun UpdateProduct(id_product: Int, product: Product): Product {
        val token = sessionManager.getToken()
        val productDto = product.toDto()
        return api.updateProduct("Bearer $token", id_product, productDto).toDomain()
    }

    override suspend fun DeleteProduct(id: Int): Boolean {
        val token = sessionManager.getToken()
        api.deleteProduct("Bearer $token", id)
        return true
    }
}

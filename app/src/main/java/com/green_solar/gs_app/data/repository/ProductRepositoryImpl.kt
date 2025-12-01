package com.green_solar.gs_app.data.repository

import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.dto.ProductCreateRequest
import com.green_solar.gs_app.data.remote.dto.ProductUpdateRequest
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.model.ProductCategory
import com.green_solar.gs_app.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val api: ApiService,
    private val session: SessionManager
) : ProductRepository {

    override suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        api.getAllProducts().map { it.toDomain() }
    }

    override suspend fun getProductById(id: Long): Result<Product> = runCatching {
        api.getProductById(id).toDomain()
    }

    override suspend fun getAllCategories(): Result<List<ProductCategory>> = runCatching {
        api.getAllCategories()
    }

    override suspend fun searchProducts(name: String?, category: ProductCategory?): Result<List<Product>> = runCatching {
        api.searchProducts(name, category).map { it.toDomain() }
    }

    override suspend fun createProduct(productData: Any): Result<Product> = runCatching {
        val token = session.getToken() ?: throw Exception("Admin token required")
        val request = productData as ProductCreateRequest // We expect the correct type from the caller
        api.createProduct("Bearer $token", request).toDomain()
    }

    override suspend fun updateProduct(id: Long, productData: Any): Result<Product> = runCatching {
        val token = session.getToken() ?: throw Exception("Admin token required")
        val request = productData as ProductUpdateRequest
        api.updateProduct("Bearer $token", id, request).toDomain()
    }

    override suspend fun deleteProduct(id: Long): Result<Unit> = runCatching {
        val token = session.getToken() ?: throw Exception("Admin token required")
        api.deleteProduct("Bearer $token", id)
    }
}

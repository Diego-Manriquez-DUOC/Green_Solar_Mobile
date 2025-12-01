package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.domain.model.ProductCategory

/**
 * Interface for the Product repository.
 */
interface ProductRepository {

    /**
     * Fetches all products. This is a public endpoint.
     */
    suspend fun getAllProducts(): Result<List<Product>>

    /**
     * Fetches a single product by its ID. This is a public endpoint.
     */
    suspend fun getProductById(id: Long): Result<Product>

    /**
     * Fetches all available product categories. This is a public endpoint.
     */
    suspend fun getAllCategories(): Result<List<ProductCategory>>

    /**
     * Searches for products by name and/or category. This is a public endpoint.
     */
    suspend fun searchProducts(name: String?, category: ProductCategory?): Result<List<Product>>

    /**
     * Creates a new product. This is an admin-only endpoint.
     */
    suspend fun createProduct(productData: Any): Result<Product> // Using Any for now, will be specific in Impl

    /**
     * Updates an existing product. This is an admin-only endpoint.
     */
    suspend fun updateProduct(id: Long, productData: Any): Result<Product> // Using Any for now

    /**
     * Deletes a product. This is an admin-only endpoint.
     */
    suspend fun deleteProduct(id: Long): Result<Unit>
}

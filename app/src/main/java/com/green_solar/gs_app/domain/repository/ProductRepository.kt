package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Product

interface ProductRepository {
 suspend fun GetProducts(): List<Product>
 suspend fun CreateProduct(product: Product): Product
 suspend fun UpdateProduct(id_product: Int, product: Product): Product

 suspend fun DeleteProduct(id: Int): Boolean
}




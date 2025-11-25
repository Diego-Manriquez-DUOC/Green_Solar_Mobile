package com.green_solar.gs_app.data.remote.dto

import com.green_solar.gs_app.domain.model.Product

// Convierte un ProductDto (de la API) a un Product (modelo de dominio)
fun ProductDto.toDomain(): Product = Product(
    id = this.id.toInt(),
    title = this.name,
    description = "", // El DTO no proporciona este campo
    price = this.price,
    productCategory = "" // El DTO no proporciona este campo
)

// Convierte un Product (modelo de dominio) a un ProductDto (para enviar a la API)
fun Product.toDto(): ProductDto = ProductDto(
    id = this.id.toLong(),
    name = this.title,
    price = this.price,
    description = ""
)

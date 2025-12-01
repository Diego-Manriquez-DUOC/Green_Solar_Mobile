package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para un Producto, tal como viene de la API.
 */
data class ProductoDto(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("desc")
    val descripcion: String,

    @SerializedName("price")
    val precio: Double,

    @SerializedName("category")
    val categoria: String,

    @SerializedName("produccion")
    val produccion: Double
)

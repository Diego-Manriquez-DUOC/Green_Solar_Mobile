package com.green_solar.gs_app.domain.model

/**
 * Modelo de dominio para una Cotización. Contiene una lista de productos.
 */
data class Cotizacion(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val productos: List<Producto> = emptyList()
)

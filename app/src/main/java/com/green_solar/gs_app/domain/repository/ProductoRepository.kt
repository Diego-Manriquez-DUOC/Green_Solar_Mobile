package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Producto

/**
 * Interfaz para el repositorio de Productos.
 * Define el contrato de las operaciones de datos para los productos.
 */
interface ProductoRepository {

    /**
     * Obtiene la lista de todos los productos desde la API.
     */
    suspend fun getProductos(): List<Producto>

}

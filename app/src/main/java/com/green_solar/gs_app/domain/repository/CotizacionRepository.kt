package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.Cotizacion

/**
 * Interfaz para el repositorio de Cotizaciones.
 */
interface CotizacionRepository {

    /**
     * Obtiene la lista de todas las cotizaciones.
     */
    suspend fun getCotizaciones(): List<Cotizacion>

    /**
     * Crea una nueva cotización.
     */
    suspend fun createCotizacion(cotizacion: Cotizacion): Cotizacion
}

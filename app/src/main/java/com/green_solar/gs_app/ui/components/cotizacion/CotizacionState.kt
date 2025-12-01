package com.green_solar.gs_app.ui.components.cotizacion

import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.domain.model.Producto

/**
 * Representa el estado de la UI para la creación y visualización de cotizaciones.
 */
data class CotizacionState(
    // Para la lista de productos a seleccionar
    val isLoadingProductos: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val productosError: String? = null,

    // Para la lista de cotizaciones existentes
    val isLoadingCotizaciones: Boolean = false,
    val cotizaciones: List<Cotizacion> = emptyList(),
    val cotizacionesError: String? = null,

    // Para el proceso de creación
    val isCreating: Boolean = false,
    val creationSuccess: Boolean = false,
    val creationError: String? = null
)

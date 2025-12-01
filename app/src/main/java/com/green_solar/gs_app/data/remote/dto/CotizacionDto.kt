package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para una Cotización, tal como viene de la API.
 */
data class CotizacionDto(
    @SerializedName("cotizacion_id")
    val cotizacionId: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("lista") // Asumiendo que el campo en el JSON se llama "lista"
    val productos: List<ProductoDto> = emptyList()
)

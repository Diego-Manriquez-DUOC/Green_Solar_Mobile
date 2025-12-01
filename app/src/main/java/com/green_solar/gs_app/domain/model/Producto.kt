package com.green_solar.gs_app.domain.model

/**
 * Modelo de dominio para un Producto. Esta es la clase "limpia" que usará la app.
 */
data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val produccionKwz: Double // "produccion (Kwz)" se convierte a camelCase
)

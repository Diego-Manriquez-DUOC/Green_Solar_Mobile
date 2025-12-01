package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.CotizacionDto
import com.green_solar.gs_app.data.remote.dto.ProductoDto
import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.domain.model.Producto
import com.green_solar.gs_app.domain.model.User

/**
 * Mapea un UserDto a un User de dominio.
 */
fun UserDto.toDomain(): User {
    return User(
        user_id = this.user_id,
        name = this.name,
        email = this.email,
        role = this.role,
        img_url = this.img_url
    )
}

/**
 * Mapea un ProductoDto (de la API) a un Producto de dominio (limpio).
 */
fun ProductoDto.toDomain(): Producto {
    return Producto(
        id = this.productId,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        categoria = this.categoria,
        produccionKwz = this.produccion
    )
}

/**
 * Mapea un CotizacionDto (de la API) a una Cotizacion de dominio (limpia).
 * Internamente, usa el mapper de Producto para convertir la lista.
 */
fun CotizacionDto.toDomain(): Cotizacion {
    return Cotizacion(
        id = this.cotizacionId,
        nombre = this.nombre,
        descripcion = this.descripcion,
        productos = this.productos.map { it.toDomain() } // Mapea cada producto de la lista
    )
}

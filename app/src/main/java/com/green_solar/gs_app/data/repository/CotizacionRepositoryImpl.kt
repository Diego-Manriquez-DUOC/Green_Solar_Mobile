package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.remote.dto.CotizacionDto
import com.green_solar.gs_app.data.remote.dto.ProductoDto
import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.domain.model.Producto
import com.green_solar.gs_app.domain.repository.CotizacionRepository

/**
 * Implementación del repositorio de Cotizaciones.
 */
class CotizacionRepositoryImpl(private val context: Context) : CotizacionRepository {

    private val api: ApiService = RetrofitClient.create(context).create(ApiService::class.java)
    private val sessionManager = SessionManager(context)

    private suspend fun getAuthToken(): String {
        val token = sessionManager.getToken()
        return "Bearer ${token ?: ""}"
    }

    override suspend fun getCotizaciones(): List<Cotizacion> {
        val response = api.getCotizaciones(getAuthToken())
        return response.data.map { it.toDomain() }
    }

    override suspend fun createCotizacion(cotizacion: Cotizacion): Cotizacion {
        // Mapeamos manualmente el modelo de dominio a DTO para enviarlo a la API
        val productoDtos = cotizacion.productos.map { it.toDto() }
        val requestDto = CotizacionDto(
            cotizacionId = cotizacion.id, // El backend debería ignorar este ID al crear
            nombre = cotizacion.nombre,
            descripcion = cotizacion.descripcion,
            productos = productoDtos
        )

        val response = api.createCotizacion(getAuthToken(), requestDto)
        return response.data.toDomain()
    }
}

/**
 * Función de ayuda para mapear un Producto de dominio a su DTO.
 */
private fun Producto.toDto(): ProductoDto {
    return ProductoDto(
        productId = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        categoria = this.categoria,
        produccion = this.produccionKwz
    )
}

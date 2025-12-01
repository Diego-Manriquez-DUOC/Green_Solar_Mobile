package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.domain.model.Producto
import com.green_solar.gs_app.domain.repository.ProductoRepository

/**
 * Implementación del repositorio de Productos.
 */
class ProductoRepositoryImpl(private val context: Context) : ProductoRepository {

    private val api: ApiService = RetrofitClient.create(context).create(ApiService::class.java)
    private val sessionManager = SessionManager(context)

    private suspend fun getAuthToken(): String {
        val token = sessionManager.getToken()
        return "Bearer ${token ?: ""}"
    }

    override suspend fun getProductos(): List<Producto> {
        val response = api.getProductos(getAuthToken())
        return response.data.map { it.toDomain() }
    }
}

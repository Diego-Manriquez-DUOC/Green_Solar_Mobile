package com.green_solar.gs_app.ui.components.cotizacion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.data.repository.CotizacionRepositoryImpl
import com.green_solar.gs_app.data.repository.ProductoRepositoryImpl
import com.green_solar.gs_app.domain.repository.CotizacionRepository
import com.green_solar.gs_app.domain.repository.ProductoRepository

/**
 * Factory para crear instancias de CotizacionViewModel.
 * Versión simplificada: Inyecta los Repositorios directamente en el ViewModel.
 */
class CotizacionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CotizacionViewModel::class.java)) {
            // 1. Crear las implementaciones de los Repositorios
            val productoRepository: ProductoRepository = ProductoRepositoryImpl(context)
            val cotizacionRepository: CotizacionRepository = CotizacionRepositoryImpl(context)

            // 2. Crear y devolver la instancia del ViewModel con los repositorios
            @Suppress("UNCHECKED_CAST")
            return CotizacionViewModel(
                productoRepository = productoRepository,
                cotizacionRepository = cotizacionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.green_solar.gs_app.ui.components.cotizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.domain.repository.CotizacionRepository
import com.green_solar.gs_app.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Cotización.
 * Versión simplificada: Llama a los repositorios directamente.
 */
class CotizacionViewModel(
    private val productoRepository: ProductoRepository,
    private val cotizacionRepository: CotizacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CotizacionState())
    val state: StateFlow<CotizacionState> = _state.asStateFlow()

    fun loadProductos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProductos = true) }
            try {
                val productos = productoRepository.getProductos()
                _state.update { it.copy(isLoadingProductos = false, productos = productos) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingProductos = false, productosError = e.message) }
            }
        }
    }

    fun loadCotizaciones() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCotizaciones = true) }
            try {
                val cotizaciones = cotizacionRepository.getCotizaciones()
                _state.update { it.copy(isLoadingCotizaciones = false, cotizaciones = cotizaciones) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingCotizaciones = false, cotizacionesError = e.message) }
            }
        }
    }

    fun createCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, creationSuccess = false, creationError = null) }
            try {
                cotizacionRepository.createCotizacion(cotizacion)
                _state.update { it.copy(isCreating = false, creationSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isCreating = false, creationError = e.message) }
            }
        }
    }

    fun resetCreationStatus() {
        _state.update { it.copy(creationSuccess = false, creationError = null) }
    }
}

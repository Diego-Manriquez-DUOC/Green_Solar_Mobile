package com.green_solar.gs_app.ui.components.product

import com.green_solar.gs_app.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

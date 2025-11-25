package com.green_solar.gs_app.domain.model

data class Product (
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val productCategory: String
)

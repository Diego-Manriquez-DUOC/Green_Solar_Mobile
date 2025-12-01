package com.green_solar.gs_app.domain.model

/**
 * Domain model for a Product. This is the clean object the app will use.
 */
data class Product(
    val id: Long,
    val name: String,
    val desc: String?,
    val price: Int,
    val category: ProductCategory,
    val storageKW: Int,
    val productionKW: Int,
    val imgUrl: String?
)

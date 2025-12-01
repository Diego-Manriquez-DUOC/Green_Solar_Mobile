package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.green_solar.gs_app.domain.model.ProductCategory

/**
 * DTO for creating a new Product.
 */
data class ProductCreateRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("desc")
    val desc: String?,
    @SerializedName("price")
    val price: Int,
    @SerializedName("category")
    val category: ProductCategory,
    @SerializedName("storageKW")
    val storageKW: Int,
    @SerializedName("productionKW")
    val productionKW: Int,
    @SerializedName("imgUrl")
    val imgUrl: String?
)

/**
 * DTO for updating an existing Product.
 */
data class ProductUpdateRequest(
    @SerializedName("name")
    val name: String?,
    @SerializedName("desc")
    val desc: String?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("category")
    val category: ProductCategory?,
    @SerializedName("storageKW")
    val storageKW: Int?,
    @SerializedName("productionKW")
    val productionKW: Int?,
    @SerializedName("imgUrl")
    val imgUrl: String?
)

/**
 * DTO for the Product response from the API.
 */
data class ProductResponseDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("desc")
    val desc: String?,
    @SerializedName("price")
    val price: Int,
    @SerializedName("category")
    val category: ProductCategory,
    @SerializedName("storageKW")
    val storageKW: Int,
    @SerializedName("productionKW")
    val productionKW: Int,
    @SerializedName("imgUrl")
    val imgUrl: String?
)

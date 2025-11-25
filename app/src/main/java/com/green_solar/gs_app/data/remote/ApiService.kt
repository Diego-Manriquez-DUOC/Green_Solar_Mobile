package com.green_solar.gs_app.data.remote

import com.green_solar.gs_app.data.remote.dto.LoginRequest
import com.green_solar.gs_app.data.remote.dto.LoginResponseDto
import com.green_solar.gs_app.data.remote.dto.MeResponse
import com.green_solar.gs_app.data.remote.dto.ProductDto
import com.green_solar.gs_app.data.remote.dto.SignupRequest
import com.green_solar.gs_app.data.remote.dto.SignupResponseDto
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseDto

    @POST("auth/signup")
    suspend fun signup(@Body req: SignupRequest): SignupResponseDto

    @GET("me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): MeResponse

    @Multipart
    @PUT("me/update_img")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): MeResponse

    /*
         API Products
     */
    @GET("api/products")
    suspend fun getProducts(
        @Header("Authorization") token: String): List<ProductDto>

    @POST("api/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body product: ProductDto): ProductDto

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body product: ProductDto): ProductDto

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

}

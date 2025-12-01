package com.green_solar.gs_app.data.remote

import com.green_solar.gs_app.data.remote.dto.*
import com.green_solar.gs_app.domain.model.ProductCategory
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    /*
        Auth & User API
     */
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signup(@Body req: SignupRequest): AuthResponse

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): MeResponse

    @Multipart
    @PUT("auth/me/update_img")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): MeResponse

    /*
         Products API (Corrected based on user's controller)
     */
    // PUBLIC Endpoints (No Auth needed)
    @GET("api/products")
    suspend fun getAllProducts(): List<ProductResponseDTO>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductResponseDTO

    @GET("api/products/categories")
    suspend fun getAllCategories(): List<ProductCategory>

    @GET("api/products/search")
    suspend fun searchProducts(
        @Query("name") name: String?,
        @Query("category") category: ProductCategory?
    ): List<ProductResponseDTO>

    // ADMIN Endpoints (Auth needed)
    @POST("api/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body product: ProductCreateRequest
    ): ProductResponseDTO

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body product: ProductUpdateRequest
    ): ProductResponseDTO

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Unit

    /*
         Carts API (All endpoints need Auth)
     */
    @GET("api/carts")
    suspend fun getUserCarts(@Header("Authorization") token: String): List<CartResponse>

    @GET("api/carts/{cartId}")
    suspend fun getCart(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Long
    ): CartResponse

    @POST("api/carts")
    suspend fun createCart(
        @Header("Authorization") token: String,
        @Body cart: CartCreateRequest
    ): CartResponse

    @PUT("api/carts/{cartId}")
    suspend fun updateCart(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Long,
        @Body cart: CartUpdateRequest
    ): CartResponse

    @DELETE("api/carts/{cartId}")
    suspend fun deleteCart(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Long
    ): Unit
}

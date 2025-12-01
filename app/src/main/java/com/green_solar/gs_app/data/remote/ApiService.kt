package com.green_solar.gs_app.data.remote

import com.green_solar.gs_app.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Wrapper genérico para las respuestas de la API.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null,
    val total: Int? = null
)

interface ApiService {
    /*
        API Auth & User
     */
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
         API de Productos
     */
    @GET("productos") // Asumiendo que el endpoint es /productos
    suspend fun getProductos(@Header("Authorization") token: String): ApiResponse<List<ProductoDto>>

    /*
         API de Cotizaciones
     */
    @GET("cotizaciones") // Asumiendo que el endpoint es /cotizaciones
    suspend fun getCotizaciones(@Header("Authorization") token: String): ApiResponse<List<CotizacionDto>>

    @POST("cotizaciones") // Asumiendo que el endpoint es /cotizaciones
    suspend fun createCotizacion(
        @Header("Authorization") token: String,
        @Body cotizacion: CotizacionDto // Enviamos el DTO directamente
    ): ApiResponse<CotizacionDto>
}

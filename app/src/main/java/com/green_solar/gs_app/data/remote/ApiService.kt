package com.green_solar.gs_app.data.remote

import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.data.remote.dto.LoginRequest
import com.green_solar.gs_app.data.remote.dto.LoginResponseDto
import retrofit2.http.*
interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseDto

    @POST("auth/signup")
    suspend fun signup(@Body req: LoginRequest): LoginResponseDto

    @GET("auth/me")
    suspend fun getCurrentUser(): UserDto

    // (No existe en Xano demo, pero dejo la firma si tu dominio la pide)
    // Si luego tu backend agrega /users/{id}, podrás usarla.
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto
}
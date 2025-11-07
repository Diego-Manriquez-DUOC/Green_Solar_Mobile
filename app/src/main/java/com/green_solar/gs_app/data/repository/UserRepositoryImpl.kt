package com.green_solar.gs_app.data.repository

import android.content.Context
import android.net.Uri
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepositoryImpl(private val context: Context) : UserRepository {
    private val api = RetrofitClient.create(context).create(ApiService::class.java)
    private val session = SessionManager(context)

    override suspend fun getCurrentUser(): Result<User> =
        runCatching {
            val token = session.getToken()?.trim()
            if (token.isNullOrBlank()) {
                throw IllegalStateException("No se pudo encontrar token de autenticación.")
            }
            val response = api.getCurrentUser("Bearer $token")
            response.user.toDomain()
        }

    override suspend fun updateProfileImage(imageUri: Uri): Result<User> = runCatching {
        val token = session.getToken()?.trim()
        if (token.isNullOrBlank()) {
            throw IllegalStateException("No se pudo encontrar token de autenticación.")
        }

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalStateException("Error al cargar foto de perfil.")

        val fileBytes = inputStream.readBytes()
        inputStream.close()

        val mimeType = context.contentResolver.getType(imageUri)
        val requestBody = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())

        // En el body del request a /me/update_img pide un campo "image" con el archivo de la imagen
        val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

        val response = api.updateProfileImage("Bearer $token", body)
        response.user.toDomain()
    }
}

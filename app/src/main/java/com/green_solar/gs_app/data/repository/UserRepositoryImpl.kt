package com.green_solar.gs_app.data.repository

import android.content.Context
import android.net.Uri
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepositoryImpl(
    private val api: ApiService,
    private val session: SessionManager,
    private val context: Context
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> =
        runCatching {
            val token = session.getToken() ?: throw IllegalStateException("User not authenticated")
            val userId = session.getUserId() ?: throw IllegalStateException("User ID not found in session")

            val meResponse = api.getCurrentUser("Bearer $token")
            
            // MODIFIED: Aligned with the User.kt data class
            User(
                user_id = userId.toString(),
                username = meResponse.username, // Correct: was name
                email = meResponse.email,
                role = meResponse.role,
                imgUrl = meResponse.imgUrl      // Correct: was img_url
            )
        }

    override suspend fun updateProfileImage(imageUri: Uri): Result<User> = runCatching {
        val token = session.getToken() ?: throw IllegalStateException("User not authenticated")
        val userId = session.getUserId() ?: throw IllegalStateException("User ID not found in session")

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalStateException("Error loading profile picture.")

        val fileBytes = inputStream.readBytes()
        inputStream.close()

        val mimeType = context.contentResolver.getType(imageUri)
        val requestBody = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())

        val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

        val meResponse = api.updateProfileImage("Bearer $token", body)

        // MODIFIED: Aligned with the User.kt data class
        User(
            user_id = userId.toString(),
            username = meResponse.username,
            email = meResponse.email,
            role = meResponse.role,
            imgUrl = meResponse.imgUrl
        )
    }
}

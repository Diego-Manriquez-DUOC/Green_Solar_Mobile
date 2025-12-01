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

// MODIFIED: Constructor now accepts dependencies instead of creating them.
class UserRepositoryImpl(
    private val api: ApiService,
    private val session: SessionManager,
    private val context: Context // Still need context for ContentResolver
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> =
        runCatching {
            val token = session.getToken() ?: throw IllegalStateException("User not authenticated")
            val userId = session.getUserId() ?: throw IllegalStateException("User ID not found in session")

            val meResponse = api.getCurrentUser("Bearer $token")
            
            // Manually construct the User domain model
            User(
                user_id = userId.toString(), // Convert Long to String
                name = meResponse.username,
                email = meResponse.email,
                role = meResponse.role,
                img_url = meResponse.imgUrl
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

        // Manually construct the User domain model
        User(
            user_id = userId.toString(),
            name = meResponse.username,
            email = meResponse.email,
            role = meResponse.role,
            img_url = meResponse.imgUrl
        )
    }
}

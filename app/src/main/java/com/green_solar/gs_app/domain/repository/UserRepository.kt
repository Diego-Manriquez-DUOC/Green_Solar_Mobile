package com.green_solar.gs_app.domain.repository

import android.net.Uri
import com.green_solar.gs_app.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateProfileImage(imageUri: Uri): Result<User>
}

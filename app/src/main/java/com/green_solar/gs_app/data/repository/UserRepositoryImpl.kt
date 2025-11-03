package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.UserRepository

class UserRepositoryImpl(context: Context) : UserRepository {
    private val api = RetrofitClient.create(context).create(ApiService::class.java)

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        api.getCurrentUser().toDomain()
    }

    override suspend fun getUserById(id: Int): Result<User> = runCatching {
        api.getCurrentUser().toDomain()
    }

}

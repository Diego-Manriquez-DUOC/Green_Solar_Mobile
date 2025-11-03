package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.UserRepository

class UserRepositoryImpl(ctx: Context) : UserRepository {
    private val api = RetrofitClient.create(ctx).create(ApiService::class.java)

    override suspend fun getCurrentUser(): Result<User> =
        runCatching {
            val dto = api.getCurrentUser()   // puede lanzar HttpException/IOE
            dto.toDomain()                   // mapea a tu modelo User
        }

    override suspend fun getUserById(id: Int): Result<User> = runCatching {
        api.getUserById(id).toDomain()
    }

}

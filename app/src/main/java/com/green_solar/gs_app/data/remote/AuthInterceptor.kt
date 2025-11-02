package com.green_solar.gs_app.data.remote

import com.green_solar.gs_app.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val session : SessionManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val req = chain.request()
        val token = runBlocking { session.getToken() }
        val authenticated = if (!token.isNullOrEmpty())
            req.newBuilder().header("Authorization", "Bearer $token").build()
        else req
        return chain.proceed(authenticated)
    }
}
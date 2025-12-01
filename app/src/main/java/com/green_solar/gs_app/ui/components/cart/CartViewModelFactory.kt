package com.green_solar.gs_app.ui.components.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.repository.CartRepositoryImpl
import com.green_solar.gs_app.data.repository.ProductRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Factory to create CartViewModel instances.
 * It handles the creation of all dependencies: ApiService, SessionManager, and Repositories.
 */
class CartViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            
            // --- Dependency Creation ---

            // CORRECTED: Create an OkHttpClient with a logging interceptor to make network calls visible.
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // 1. Create ApiService instance, now using the configured OkHttpClient
            val retrofit = Retrofit.Builder()
                .baseUrl("http://45.236.131.233:22222/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // Assign the OkHttpClient to Retrofit
                .build()
            val apiService: ApiService = retrofit.create(ApiService::class.java)

            // 2. Create SessionManager instance
            val sessionManager = SessionManager(context.applicationContext)

            // 3. Create Repository instances
            val productRepository = ProductRepositoryImpl(apiService, sessionManager)
            val cartRepository = CartRepositoryImpl(apiService, sessionManager)

            // 4. Create and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                productRepository = productRepository,
                cartRepository = cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

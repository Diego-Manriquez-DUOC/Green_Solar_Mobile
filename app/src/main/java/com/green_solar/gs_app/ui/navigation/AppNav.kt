package com.green_solar.gs_app.ui.navigation

import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl
import com.green_solar.gs_app.data.repository.UserRepositoryImpl
import com.green_solar.gs_app.ui.components.auth.SignupVMFactory
import com.green_solar.gs_app.ui.components.auth.SignupViewModel
import com.green_solar.gs_app.ui.components.login.LoginVMFactory
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import com.green_solar.gs_app.ui.components.profile.ProfileVMFactory
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.screens.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
    const val SignUp = "signup"
    const val Main = "main"
    const val Projects = "projects"
    const val Monitoring = "monitoring"
    const val Quote = "quote"
    const val ManageProducts = "manage_products" // Nueva ruta para Admin
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- CENTRALIZED DEPENDENCY CREATION ---
    // These are created once and remembered across recompositions.
    val sessionManager = remember { SessionManager(ctx) }

    val apiService = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Replace with your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val authRepo = remember { AuthRepositoryImpl(apiService, sessionManager) }
    val userRepo = remember { UserRepositoryImpl(apiService, sessionManager, ctx) }
    // --- END OF DEPENDENCIES ---

    NavHost(
        navController = nav,
        startDestination = Routes.Splash,
        enterTransition = { fadeIn(animationSpec = tween(350)) },
        exitTransition = { fadeOut(animationSpec = tween(350)) },
        popEnterTransition = { fadeIn(animationSpec = tween(350)) },
        popExitTransition = { fadeOut(animationSpec = tween(350)) }
    ) {

        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val hasToken = sessionManager.hasToken() // Use the centralized instance
                val dest = if (hasToken) Routes.Main else Routes.Login
                nav.navigate(dest) {
                    popUpTo(Routes.Splash) { inclusive = true }
                    launchSingleTop = true
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(Routes.Login) {
            // The authRepo is already created above. We just pass it to the factory.
            val vm: LoginViewModel = viewModel(factory = LoginVMFactory(authRepo))

            LoginScreen(
                vm = vm,
                onLoggedIn = {
                    nav.navigate(Routes.Main) {
                        popUpTo(nav.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = { nav.navigate(Routes.SignUp) }
            )
        }

        composable(Routes.SignUp) {
            // Pass the centralized authRepo to the factory.
            val vm: SignupViewModel = viewModel(factory = SignupVMFactory(authRepo))

            SignupScreen(
                viewModel = vm,
                onRegistered = {
                    nav.navigate(Routes.Login) {
                        popUpTo(nav.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoginClicked = { nav.popBackStack() }
            )
        }

        composable(Routes.Profile) {
            val app = ctx.applicationContext as Application
            // Pass the centralized repos to the factory.
            val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(app, userRepo, authRepo))

            ProfileScreen(
                viewModel = vm,
                nav = nav,
            )
        }

        composable(Routes.Main) {
            val app = ctx.applicationContext as Application
            // Pass the centralized repos to the factory.
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileVMFactory(app, userRepo, authRepo))

            MainScreen(
                nav = nav,
                onLogout = {
                    scope.launch {
                        authRepo.logout() // Use the centralized repo
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                profileViewModel = profileViewModel
            )
        }

        composable(Routes.Projects) {
            ProjectsScreen(nav = nav)
        }

        composable(Routes.Monitoring) {
            MonitoringScreen(nav = nav)
        }

        composable(Routes.Quote) {
            CartScreen(nav = nav)
        }

        composable(Routes.ManageProducts) {
            ManageProductsScreen(nav = nav)
        }
    }
}

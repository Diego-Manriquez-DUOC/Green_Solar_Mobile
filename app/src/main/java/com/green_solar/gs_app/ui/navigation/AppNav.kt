package com.green_solar.gs_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl
import com.green_solar.gs_app.data.repository.UserRepositoryImpl
import com.green_solar.gs_app.ui.components.login.LoginVMFactory
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import com.green_solar.gs_app.ui.components.profile.ProfileVMFactory
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.screens.LoginScreen
import com.green_solar.gs_app.ui.screens.ProfileScreen

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = LocalContext.current

    NavHost(navController = nav, startDestination = Routes.Splash) {

        // 1) Splash: decide si hay token guardado
        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val hasToken = SessionManager(ctx).hasToken()
                if (hasToken) {
                    nav.navigate(Routes.Profile) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                } else {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // 2) Login
        composable(Routes.Login) {
            val authRepo = remember(ctx) { AuthRepositoryImpl(ctx) }
            val vm: LoginViewModel = viewModel(factory = LoginVMFactory(authRepo))

            LoginScreen(
                vm = vm,
                onLoggedIn = {
                    nav.navigate(Routes.Profile) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

// 3) Profile
        composable(Routes.Profile) {
            val ctx = LocalContext.current

            // Repos
            val userRepo = remember(ctx) { com.green_solar.gs_app.data.repository.UserRepositoryImpl(ctx) }
            val authRepo = remember(ctx) { com.green_solar.gs_app.data.repository.AuthRepositoryImpl(ctx) }

            // ViewModel con ambos repos
            val vm: com.green_solar.gs_app.ui.components.profile.ProfileViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.green_solar.gs_app.ui.components.profile.ProfileVMFactory(userRepo, authRepo)
                )

            com.green_solar.gs_app.ui.screens.ProfileScreen(
                viewModel = vm,
                onLogout = {
                    // Navega al Login y limpia el backstack
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Profile) { inclusive = true }
                    }
                }
            )
        }
    }
}
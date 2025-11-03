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
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl
import com.green_solar.gs_app.data.repository.UserRepositoryImpl
import com.green_solar.gs_app.ui.components.auth.SignupVMFactory
import com.green_solar.gs_app.ui.components.auth.SignupViewModel
import com.green_solar.gs_app.ui.components.login.LoginVMFactory
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import com.green_solar.gs_app.ui.components.profile.ProfileVMFactory
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.screens.*
import com.green_solar.gs_app.data.local.SessionManager
import kotlinx.coroutines.launch

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
    const val SignUp = "signup"
    const val Main = "main"
    const val Projects = "projects"
    const val Monitoring = "monitoring"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // PASO 2: Añadido un fundido cruzado (Crossfade) a todas las transiciones.
    // Esto hará que el cambio de pantalla al cerrar sesión sea suave y elegante.
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
                val hasToken = SessionManager(ctx).hasToken()
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
            val authRepo = remember(ctx) { AuthRepositoryImpl(ctx) }
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
            val authRepo = remember(ctx) { AuthRepositoryImpl(ctx) }
            val vm: SignupViewModel = viewModel(factory = SignupVMFactory(authRepo))

            SignupScreen(
                viewModel = vm,
                onRegistered = {
                    nav.navigate(Routes.Login) {
                        popUpTo(nav.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Profile) {
            val app = ctx.applicationContext as Application
            val userRepo = remember(ctx) { UserRepositoryImpl(ctx) }
            val authRepo = remember(ctx) { AuthRepositoryImpl(ctx) }
            val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(app, userRepo, authRepo))

            ProfileScreen(
                viewModel = vm,
                onLogout = {
                    scope.launch {
                        vm.logout() // El ViewModel ya se encarga de llamar al repo y limpiar la sesión
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Routes.Main) {
            // ✅ Refactorizado: Usamos el repositorio para una lógica de logout consistente
            val authRepo = remember { AuthRepositoryImpl(ctx) }
            MainScreen(
                nav = nav,
                onLogout = {
                    scope.launch {
                        authRepo.logout()
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Routes.Projects) {
            ProjectsScreen(onNavigateBack = { nav.popBackStack() })
        }

        composable(Routes.Monitoring) {
            MonitoringScreen(onNavigateBack = { nav.popBackStack() })
        }
    }
}

package com.green_solar.gs_app.ui.navigation

import android.app.Application
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

/**
 * Objeto que centraliza todas las rutas de navegación de la aplicación como constantes.
 * Esto evita errores de tipeo y facilita la gestión de las rutas.
 */
object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
    const val SignUp = "signup"
    const val Main = "main"
    const val Projects = "projects"
    const val Monitoring = "monitoring"
}

/**
 * Composable principal que define el grafo de navegación de toda la aplicación.
 * Utiliza un NavHost para gestionar las transiciones entre las diferentes pantallas (composables).
 */
@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    // Obtenemos el CoroutineScope para lanzar operaciones asíncronas de forma segura
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.Splash) {

        /**
         * Define la pantalla de Splash.
         * Su única función es verificar si existe un token de sesión y redirigir al usuario
         * a la pantalla principal (Main) si está logueado, o a la de Login si no lo está.
         */
        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val dest = if (SessionManager(ctx).hasToken()) Routes.Main else Routes.Login
                nav.navigate(dest) {
                    popUpTo(Routes.Splash) { inclusive = true }
                    launchSingleTop = true
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        /**
         * Define la pantalla de Login.
         * Inyecta el ViewModel necesario y gestiona la navegación hacia la pantalla principal
         * en caso de un login exitoso, o hacia la pantalla de registro.
         */
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

        /**
         * Define la pantalla de Registro (SignUp).
         * Inyecta el ViewModel correspondiente y, tras un registro exitoso, navega
         * de vuelta a la pantalla de Login, limpiando el historial para que el usuario no pueda volver.
         */
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

        /**
         * Define la pantalla de Perfil de Usuario.
         * Inyecta el ViewModel con todas sus dependencias y gestiona la lógica de Logout,
         * que incluye limpiar la sesión y redirigir a la pantalla de Login.
         */
        composable(Routes.Profile) {
            val app = ctx.applicationContext as Application
            val userRepo = remember(ctx) { UserRepositoryImpl(ctx) }
            val authRepo = remember(ctx) { AuthRepositoryImpl(ctx) }
            val vm: ProfileViewModel = viewModel(
                factory = ProfileVMFactory(app, userRepo, authRepo)
            )

            ProfileScreen(
                viewModel = vm,
                onLogout = { // La llamada a .clear() debe estar en una coroutine
                    scope.launch {
                        vm.logout()
                        SessionManager(ctx).clear()
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        /**
         * Define la pantalla Principal de la aplicación (la que se ve después del login).
         * También gestiona una opción de Logout desde aquí.
         */
        composable(Routes.Main) {
            MainScreen(
                nav = nav,
                onLogout = { // La llamada a .clear() debe estar en una coroutine
                    scope.launch {
                        SessionManager(ctx).clear()
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        /**
         * Define la pantalla maquetada para "Mis Proyectos".
         * Incluye una barra de navegación con un botón para volver a la pantalla anterior.
         */
        composable(Routes.Projects) {
            ProjectsScreen(onNavigateBack = { nav.popBackStack() })
        }

        /**
         * Define la pantalla maquetada para "Monitoreo y Ahorro".
         * Incluye una barra de navegación con un botón para volver a la pantalla anterior.
         */
        composable(Routes.Monitoring) {
            MonitoringScreen(onNavigateBack = { nav.popBackStack() })
        }
    }
}

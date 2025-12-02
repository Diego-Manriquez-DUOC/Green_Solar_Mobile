package com.green_solar.gs_app.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.ui.components.auth.SignupVMFactory
import com.green_solar.gs_app.ui.components.auth.SignupViewModel
import com.green_solar.gs_app.ui.components.login.LoginVMFactory
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import com.green_solar.gs_app.ui.components.profile.ProfileVMFactory
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.screens.*
import kotlinx.coroutines.launch

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
    const val SignUp = "signup"
    const val Main = "main"
    const val Projects = "projects"
    const val Monitoring = "monitoring"
    const val Quote = "quote"
    const val EditQuote = "edit_quote"
    const val ManageProducts = "manage_products"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    NavHost(
        navController = nav,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            val session = SessionManager(ctx)
            LaunchedEffect(Unit) {
                val dest = if (session.hasToken()) Routes.Main else Routes.Login
                nav.navigate(dest) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
            // Show a loading indicator while the check is running
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable(Routes.Login) {
            val vm: LoginViewModel = viewModel(factory = LoginVMFactory(ctx))
            LoginScreen(
                vm = vm,
                onLoggedIn = {
                    nav.navigate(Routes.Main) {
                        popUpTo(nav.graph.id) { inclusive = true }
                    }
                },
                onRegisterClick = { nav.navigate(Routes.SignUp) }
            )
        }

        composable(Routes.SignUp) {
            val vm: SignupViewModel = viewModel(factory = SignupVMFactory(ctx))
            SignupScreen(
                viewModel = vm,
                onRegistered = { nav.navigate(Routes.Login) },
                onLoginClicked = { nav.popBackStack() }
            )
        }

        composable(Routes.Profile) {
            val app = ctx.applicationContext as Application
            val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(app))
            ProfileScreen(
                viewModel = vm,
                nav = nav,
            )
        }

        composable(Routes.Main) {
            val app = ctx.applicationContext as Application
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileVMFactory(app))
            MainScreen(
                nav = nav,
                onLogout = {
                    scope.launch {
                        profileViewModel.logout()
                        nav.navigate(Routes.Login) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    }
                },
                profileViewModel = profileViewModel
            )
        }

        composable("${Routes.Projects}/{newCartId}", arguments = listOf(navArgument("newCartId") { type = NavType.IntType })) {
            val newCartId = it.arguments?.getInt("newCartId")
            ProjectsScreen(nav = nav, expandedCartId = newCartId)
        }

        composable(Routes.Projects) {
            ProjectsScreen(nav = nav, expandedCartId = null)
        }

        composable(Routes.Monitoring) {
             MonitoringScreen(nav = nav)
        }

        composable(Routes.Quote) {
            CreateCotizacionScreen(nav = nav)
        }

        composable("${Routes.EditQuote}/{cotizacionId}", arguments = listOf(navArgument("cotizacionId") { type = NavType.LongType })) {
            val cotizacionId = it.arguments?.getLong("cotizacionId")
            cotizacionId?.let {
                EditCotizacionScreen(nav = nav, cotizacionId = it)
            }
        }

        composable(Routes.ManageProducts) {
             ManageProductsScreen(nav = nav)
        }
    }
}

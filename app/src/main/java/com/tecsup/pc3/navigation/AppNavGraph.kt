package com.tecsup.pc3.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.pc3.data.auth.AuthRepository
import com.tecsup.pc3.data.dashboard.DashboardRepository
import com.tecsup.pc3.data.session.SessionManager
import com.tecsup.pc3.ui.auth.AuthViewModel
import com.tecsup.pc3.ui.auth.AuthViewModelFactory
import com.tecsup.pc3.ui.auth.LoginScreen
import com.tecsup.pc3.ui.dashboard.DashboardScreen
import com.tecsup.pc3.ui.dashboard.DashboardViewModel
import com.tecsup.pc3.ui.dashboard.DashboardViewModelFactory
import com.tecsup.pc3.ui.notification.NotificationScreen
import com.tecsup.pc3.ui.placeholder.PlaceholderScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { AuthRepository(sessionManager) }
    val session by sessionManager.session.collectAsState(initial = null)

    if (session == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val loggedIn = session?.isLoggedIn == true
    NavHost(
        navController = navController,
        startDestination = if (loggedIn) AppDestination.Dashboard.route else AppDestination.Login.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Login.route) {
            if (loggedIn) {
                LaunchedEffect(Unit) { navController.replaceAll(AppDestination.Dashboard.route) }
                return@composable
            }
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository))
            val state by authViewModel.uiState.collectAsState()
            LaunchedEffect(state.authenticatedUsername) {
                if (state.authenticatedUsername != null) navController.replaceAll(AppDestination.Dashboard.route)
            }
            LoginScreen(state, authViewModel::onUsernameChange, authViewModel::onPasswordChange, authViewModel::login)
        }

        protectedComposable(AppDestination.Dashboard, loggedIn, navController) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(remember { DashboardRepository() }),
            )
            val state by dashboardViewModel.uiState.collectAsState()
            DashboardScreen(
                username = session?.username.orEmpty().ifBlank { "Wash" },
                uiState = state,
                onRetry = dashboardViewModel::loadDashboard,
                onOpenWarehouse = { navController.navigate(AppDestination.Warehouse.route) },
                onOpenBoxes = { navController.navigate(AppDestination.Boxes.route) },
                onOpenShipments = { navController.navigate(AppDestination.Shipments.route) },
                onOpenCar = { navController.navigate(AppDestination.Car.route) },
                onOpenSettings = { navController.navigate(AppDestination.Settings.route) },
                onOpenNotifications = { navController.navigate(AppDestination.Notifications.route) },
                onLogout = {
                    scope.launch {
                        authRepository.logout()
                        navController.replaceAll(AppDestination.Login.route)
                    }
                },
            )
        }

        placeholder(AppDestination.Warehouse, "Almacén", "Consulta ubicaciones y existencias.", loggedIn, navController)
        placeholder(AppDestination.Boxes, "Cajas", "Gestiona el inventario de cajas.", loggedIn, navController)
        placeholder(AppDestination.Shipments, "Despachos", "Supervisa las salidas programadas.", loggedIn, navController)
        placeholder(AppDestination.Car, "Carro IoT", "Monitorea el carro logístico.", loggedIn, navController)
        placeholder(AppDestination.Settings, "Configuración", "Administra las preferencias del sistema.", loggedIn, navController)
        protectedComposable(AppDestination.Notifications, loggedIn, navController) {
            NotificationScreen(onBack = navController::navigateUp)
        }
    }
}

private fun NavGraphBuilder.protectedComposable(
    destination: AppDestination,
    loggedIn: Boolean,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    composable(destination.route) {
        if (loggedIn) content()
        else LaunchedEffect(Unit) { navController.replaceAll(AppDestination.Login.route) }
    }
}

private fun NavGraphBuilder.placeholder(
    destination: AppDestination,
    title: String,
    description: String,
    loggedIn: Boolean,
    navController: NavHostController,
) = protectedComposable(destination, loggedIn, navController) {
    PlaceholderScreen(title, description, navController::navigateUp)
}

private fun NavHostController.replaceAll(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

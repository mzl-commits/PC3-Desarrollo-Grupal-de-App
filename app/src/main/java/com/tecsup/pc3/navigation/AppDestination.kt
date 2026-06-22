package com.tecsup.pc3.navigation

sealed class AppDestination(val route: String) {
    data object Login : AppDestination("login")
    data object Dashboard : AppDestination("dashboard")
    data object Warehouse : AppDestination("almacen")
    data object Boxes : AppDestination("cajas")
    data object Shipments : AppDestination("despachos")
    data object Car : AppDestination("carro")
    data object Settings : AppDestination("configuracion")
    data object Notifications : AppDestination("notificaciones")
}

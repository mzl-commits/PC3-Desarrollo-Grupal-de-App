package com.tecsup.pc3.data.dashboard

import com.tecsup.pc3.data.remote.LogisticsApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

data class DashboardData(
    val carStatus: String,
    val totalBoxes: Int,
    val totalShipments: Int,
    val recentMovements: List<DashboardMovement>,
    val alerts: List<DashboardAlert>,
)

data class DashboardMovement(val id: String, val description: String, val timeLabel: String)
data class DashboardAlert(val title: String, val description: String, val isCritical: Boolean = false)

sealed interface DashboardResult {
    data class Success(val data: DashboardData) : DashboardResult
    data class DemoFallback(val data: DashboardData, val reason: String) : DashboardResult
}

/** Centraliza el origen de datos para reemplazar el demo por Django sin cambiar la UI. */
class DashboardRepository(private val logisticsApi: LogisticsApi? = null) {
    suspend fun getDashboard(): DashboardResult {
        val api = logisticsApi
        if (api == null) {
            return DashboardResult.DemoFallback(demoData(), "Mostrando datos demo")
        }
        return runCatching { loadRemoteDashboard(api) }
            .fold(
                onSuccess = { DashboardResult.Success(it) },
                onFailure = {
                    DashboardResult.DemoFallback(
                        data = demoData(),
                        reason = "Backend no disponible; mostrando datos demo",
                    )
                },
            )
    }

    private suspend fun loadRemoteDashboard(api: LogisticsApi): DashboardData = coroutineScope {
        val car = async { api.getCarStatus() }
        val boxes = async { api.getBoxes() }
        val shipments = async { api.getShipments() }
        val history = async { api.getHistory() }
        DashboardData(
            carStatus = car.await().estado,
            totalBoxes = boxes.await().count,
            totalShipments = shipments.await().count,
            recentMovements = history.await().results.take(4).map {
                DashboardMovement(
                    id = it.id_log.toString(),
                    description = "${it.estado_anterior} → ${it.estado_nuevo}",
                    timeLabel = it.fecha_cambio,
                )
            },
            alerts = emptyList(),
        )
    }

    private suspend fun demoData(): DashboardData {
        delay(450)
        return DashboardData(
            carStatus = "Operativo · En base",
            totalBoxes = 128,
            totalShipments = 24,
            recentMovements = listOf(
                DashboardMovement("MOV-1048", "Caja CX-018 almacenada", "Hace 8 min"),
                DashboardMovement("MOV-1047", "Despacho DSP-024 confirmado", "Hace 22 min"),
                DashboardMovement("MOV-1046", "Carro retornó a base", "Hace 35 min"),
                DashboardMovement("MOV-1045", "Caja CX-017 clasificada", "Hace 1 h"),
            ),
            alerts = listOf(
                DashboardAlert("Carro detenido", "Revisar el carro IoT en el sector B", true),
                DashboardAlert("Caja pendiente", "CX-021 espera asignación de despacho"),
            ),
        )
    }
}

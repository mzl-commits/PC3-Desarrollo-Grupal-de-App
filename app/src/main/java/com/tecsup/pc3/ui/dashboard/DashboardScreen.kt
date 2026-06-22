package com.tecsup.pc3.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.pc3.data.dashboard.DashboardData
import com.tecsup.pc3.ui.components.AlertCard
import com.tecsup.pc3.ui.components.ErrorState
import com.tecsup.pc3.ui.components.LoadingState
import com.tecsup.pc3.ui.components.QuickAccessCard
import com.tecsup.pc3.ui.components.SectionTitle
import com.tecsup.pc3.ui.components.SummaryCard

@Composable
fun DashboardScreen(
    username: String,
    uiState: DashboardUiState,
    onRetry: () -> Unit,
    onOpenWarehouse: () -> Unit,
    onOpenBoxes: () -> Unit,
    onOpenShipments: () -> Unit,
    onOpenCar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        when (uiState) {
            DashboardUiState.Loading -> LoadingState(Modifier.padding(padding))
            is DashboardUiState.Error -> ErrorState(uiState.message, onRetry, Modifier.padding(padding))
            is DashboardUiState.Success -> DashboardContent(username, uiState.data, null, onOpenWarehouse,
                onOpenBoxes, onOpenShipments, onOpenCar, onOpenSettings, onOpenNotifications, onLogout,
                Modifier.padding(padding))
            is DashboardUiState.DemoFallback -> DashboardContent(username, uiState.data, uiState.message,
                onOpenWarehouse, onOpenBoxes, onOpenShipments, onOpenCar, onOpenSettings,
                onOpenNotifications, onLogout, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DashboardContent(
    username: String,
    data: DashboardData,
    fallbackMessage: String?,
    onOpenWarehouse: () -> Unit,
    onOpenBoxes: () -> Unit,
    onOpenShipments: () -> Unit,
    onOpenCar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text("Hola, $username", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Resumen de la operación", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = onLogout) { Text("Salir") }
        }
        fallbackMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(18.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.padding(20.dp)) {
                Text("ESTADO DEL CARRO", style = MaterialTheme.typography.labelMedium)
                Text(data.carStatus, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Cajas registradas", data.totalBoxes.toString(), Modifier.weight(1f))
            SummaryCard("Despachos", data.totalShipments.toString(), Modifier.weight(1f))
        }
        SectionTitle("Accesos rápidos", "Gestiona las áreas principales")
        QuickRow("Almacén", "Ubicaciones", onOpenWarehouse, "Cajas", "Inventario", onOpenBoxes)
        Spacer(Modifier.height(12.dp))
        QuickRow("Despachos", "Salidas", onOpenShipments, "Carro IoT", "Monitoreo", onOpenCar)
        Spacer(Modifier.height(12.dp))
        QuickRow("Configuración", "Preferencias", onOpenSettings, "Notificaciones", "Alertas", onOpenNotifications)
        SectionTitle("Alertas", "Eventos que requieren atención")
        data.alerts.forEach {
            AlertCard(it.title, it.description, it.isCritical)
            Spacer(Modifier.height(8.dp))
        }
        SectionTitle("Últimos movimientos", "Actividad reciente")
        Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                data.recentMovements.forEachIndexed { index, movement ->
                    Column(Modifier.padding(vertical = 13.dp)) {
                        Text(movement.description, fontWeight = FontWeight.Medium)
                        Text("${movement.id} · ${movement.timeLabel}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (index != data.recentMovements.lastIndex) Spacer(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun QuickRow(
    firstTitle: String, firstSubtitle: String, firstClick: () -> Unit,
    secondTitle: String, secondSubtitle: String, secondClick: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickAccessCard(firstTitle, firstSubtitle, firstClick, Modifier.weight(1f))
        QuickAccessCard(secondTitle, secondSubtitle, secondClick, Modifier.weight(1f))
    }
}

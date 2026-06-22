package com.tecsup.pc3.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.pc3.data.notification.DemoAlerts
import com.tecsup.pc3.ui.components.AlertCard
import com.tecsup.pc3.ui.components.SectionTitle
import com.tecsup.pc3.utils.NotificationHelper

@Composable
fun NotificationScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val helper = remember { NotificationHelper(context.applicationContext) }
    val permission = rememberNotificationPermissionState()

    LaunchedEffect(Unit) { helper.createChannel() }
    LaunchedEffect(permission.isGranted) {
        if (permission.isGranted) helper.show("Carro detenido", "Alerta demo: revisar carro IoT")
    }

    Scaffold(modifier = modifier) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
        ) {
            Text("Notificaciones", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                if (permission.isGranted) "Permiso concedido" else "Permiso pendiente",
                color = if (permission.isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (permission.isGranted) helper.show("Nueva alerta logística", "Notificación local de prueba")
                        else permission.requestPermission()
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Probar notificación") }
                OutlinedButton(onClick = onBack) { Text("Volver") }
            }
            SectionTitle("Alertas recientes")
            DemoAlerts.all.forEach {
                AlertCard(it.title, "${it.description} · ${it.severity}", it.severity == "Alta")
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

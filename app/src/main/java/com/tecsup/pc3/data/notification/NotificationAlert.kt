package com.tecsup.pc3.data.notification

data class NotificationAlert(
    val title: String,
    val description: String,
    val severity: String,
)

object DemoAlerts {
    val all = listOf(
        NotificationAlert("Carro detenido", "El carro IoT requiere revisión", "Alta"),
        NotificationAlert("Caja pendiente de despacho", "La caja CX-021 sigue en espera", "Media"),
        NotificationAlert("Nueva alerta logística", "Se registró actividad en el almacén", "Informativa"),
    )
}

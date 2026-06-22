# PC3 - Desarrollo Grupal de App

Aplicación logística Android desarrollada con Kotlin y Jetpack Compose.

## Módulo Wash

Incluye autenticación demo (`wash` / `123456`), Login y Dashboard principal con información logística y datos de respaldo. La navegación protege las pantallas internas y elimina el historial al cerrar sesión.

La sesión local se conserva con DataStore sin almacenar contraseñas. El módulo de Notificaciones crea un canal local, solicita permiso en Android 13+ y permite probar alertas del carro, cajas y operación logística.

La capa remota usa Retrofit y queda preparada para integrar el backend Django en `http://10.0.2.2:8000`.

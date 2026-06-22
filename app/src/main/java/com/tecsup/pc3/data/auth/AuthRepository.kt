package com.tecsup.pc3.data.auth

import com.tecsup.pc3.data.session.SessionManager
import kotlinx.coroutines.delay

data class AuthenticatedUser(
    val username: String,
    val accessToken: String? = null,
)

sealed interface AuthResult {
    data class Success(val user: AuthenticatedUser) : AuthResult
    data class Error(val message: String) : AuthResult
}

internal object DemoCredentials {
    const val USERNAME = "wash"
    const val PASSWORD = "123456"

    fun areValid(username: String, password: String): Boolean =
        username == USERNAME && password == PASSWORD
}

/** Contract that a future JWT Retrofit data source must implement. */
fun interface AuthRemoteDataSource {
    suspend fun login(username: String, password: String): AuthenticatedUser
}

class AuthRepository(
    private val sessionManager: SessionManager,
    private val remoteDataSource: AuthRemoteDataSource? = null,
) {
    suspend fun login(username: String, password: String): AuthResult {
        val result = remoteDataSource?.let { remote ->
            runCatching { remote.login(username, password) }
                .fold(
                    onSuccess = { AuthResult.Success(it) },
                    onFailure = { AuthResult.Error("No se pudo iniciar sesión. Intenta nuevamente.") },
                )
        } ?: run {
            // Credenciales temporales mientras se define el contrato JWT del backend.
            delay(DEMO_LOGIN_DELAY_MS)
            if (DemoCredentials.areValid(username, password)) {
                AuthResult.Success(AuthenticatedUser(username = username))
            } else {
                AuthResult.Error("Usuario o contraseña incorrectos")
            }
        }

        if (result is AuthResult.Success) {
            // Solo se persiste identidad básica; nunca la contraseña.
            sessionManager.saveSession(result.user.username)
        }
        return result
    }

    suspend fun logout() = sessionManager.clearSession()

    private companion object {
        const val DEMO_LOGIN_DELAY_MS = 700L
    }
}

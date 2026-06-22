package com.tecsup.pc3.ui.auth

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authenticatedUsername: String? = null,
)

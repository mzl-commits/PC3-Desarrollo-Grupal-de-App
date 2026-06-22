package com.tecsup.pc3.ui.dashboard

import com.tecsup.pc3.data.dashboard.DashboardData

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
    data class DemoFallback(val data: DashboardData, val message: String) : DashboardUiState
}

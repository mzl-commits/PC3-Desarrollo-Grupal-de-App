package com.tecsup.pc3.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.pc3.data.dashboard.DashboardRepository
import com.tecsup.pc3.data.dashboard.DashboardResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            _uiState.value = runCatching { repository.getDashboard() }.fold(
                onSuccess = { result ->
                    when (result) {
                        is DashboardResult.Success -> DashboardUiState.Success(result.data)
                        is DashboardResult.DemoFallback -> DashboardUiState.DemoFallback(result.data, result.reason)
                    }
                },
                onFailure = { DashboardUiState.Error("No se pudo cargar el dashboard") },
            )
        }
    }
}

class DashboardViewModelFactory(private val repository: DashboardRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DashboardViewModel::class.java))
        return DashboardViewModel(repository) as T
    }
}

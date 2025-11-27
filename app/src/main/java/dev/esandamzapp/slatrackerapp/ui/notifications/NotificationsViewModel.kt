package dev.esandamzapp.slatrackerapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(private val userId: Int) : ViewModel() {

    // El ViewModel ahora depende de una instancia del repositorio.
    private val repository = AlertRepository()

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            try {
                // Llamamos al método suspend del repositorio, que ya usa Retrofit
                val alerts = repository.getAlerts(userId)
                _uiState.value = NotificationUiState.Success(alerts.map { it.toNotificationSLA() })
            } catch (e: Exception) {
                // Si algo sale mal en la llamada de red (ej. sin conexión, error 500), lo capturamos aquí.
                _uiState.value = NotificationUiState.Error(e.message ?: "Error de red desconocido")
            }
        }
    }
}

// --- Clases para representar el estado de la UI (sin cambios) ---

sealed interface NotificationUiState {
    data class Success(val notifications: List<NotificationSLA>) : NotificationUiState
    data class Error(val message: String) : NotificationUiState
    object Loading : NotificationUiState
}

package dev.esandamzapp.slatrackerapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    // Hardcoded userId for now, this should be provided by your authentication system.
    private val repository = AlertRepository(userId = 5)

    private val _notifications = MutableStateFlow<List<Notificacion>>(emptyList())
    val notifications: StateFlow<List<Notificacion>> = _notifications.asStateFlow()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            repository.getByUser().onSuccess {
                val mappedNotifications = it.map { alertDto ->
                    Notificacion(
                        titulo = alertDto.nivel ?: "Notificación",
                        descripcion = alertDto.mensaje ?: "",
                        fecha = alertDto.fechaCreacion ?: "",
                        // This mapping is an assumption. You might need to adjust it based on your business logic.
                        tipo = when (alertDto.idEstadoAlerta) {
                            1 -> TipoSLA.CUMPLIDO
                            2 -> TipoSLA.POR_VENCER
                            3 -> TipoSLA.INCUMPLIDO
                            else -> TipoSLA.POR_VENCER
                        }
                    )
                }
                _notifications.value = mappedNotifications
            }.onFailure {
                // Handle error, e.g., show a toast or a snackbar
            }
        }
    }
}

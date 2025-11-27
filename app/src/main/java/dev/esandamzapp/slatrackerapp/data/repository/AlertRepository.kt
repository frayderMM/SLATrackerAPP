package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.AlertDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertRepository {

    // Usamos el apiService de Retrofit, que ya está configurado en ApiClient
    private val apiService = ApiClient.apiService

    /**
     * Obtiene las alertas para un usuario específico desde el backend.
     * La llamada se ejecuta en un hilo de IO para no bloquear la UI.
     */
    suspend fun getAlerts(userId: Int): List<AlertDto> {
        // withContext(Dispatchers.IO) es la forma segura de llamar a suspend functions de red.
        return withContext(Dispatchers.IO) {
            apiService.getAlertsByUser(userId)
        }
    }
    
    // Aquí se podrían añadir más funciones para interactuar con la API, como:
    // suspend fun markAlertAsRead(alertId: Int, userId: Int) { ... }
}

package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.SlaRequest
import retrofit2.HttpException

class SlaRepository {

    private val apiService = ApiClient.apiService

    suspend fun createSla(request: SlaRequest): Result<Unit> {
        return try {
            val response = apiService.createSla(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            // Errores HTTP (4xx, 5xx)
            when {
                e.code() in 300..399 -> Result.failure(Exception("Error de redirección: ${e.code()}. Contacta al administrador."))
                e.code() in 400..499 -> Result.failure(Exception("Error en la petición: ${e.code()}. Verifica los datos enviados."))
                e.code() in 500..599 -> Result.failure(Exception("Error del servidor: ${e.code()}. Inténtalo más tarde."))
                else -> Result.failure(Exception("Error HTTP: ${e.code()}"))
            }
        } catch (e: Exception) {
            // Otros errores (conectividad, etc.)
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }
}

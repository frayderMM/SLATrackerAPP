package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.SlaRequest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SlaRepository {

    private val client = ApiClient.client

    suspend fun createSla(request: SlaRequest): Result<Unit> {
        return try {
            val response = client.post("http://10.0.2.2:5000/api/solicitud") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.status.value} ${response.status.description}"))
            }
        } catch (e: RedirectResponseException) {
            // 3xx - Redirecciones
            Result.failure(Exception("Error de redirección: ${e.response.status.value}. Contacta al administrador."))
        } catch (e: ClientRequestException) {
            // 4xx - Errores del cliente
            Result.failure(Exception("Error en la petición: ${e.response.status.value}. Verifica los datos enviados."))
        } catch (e: ServerResponseException) {
            // 5xx - Errores del servidor
            Result.failure(Exception("Error del servidor: ${e.response.status.value}. Inténtalo más tarde."))
        } catch (e: Exception) {
            // Otros errores (conectividad, etc.)
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }
}

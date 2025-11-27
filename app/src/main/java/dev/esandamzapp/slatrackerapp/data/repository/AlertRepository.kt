package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.AlertDto
import dev.esandamzapp.slatrackerapp.data.remote.dto.UnreadCountResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AlertRepository(private val userId: Int) {

    private val client = ApiClient.client
    private val baseUrl = "http://10.0.2.2:5000/api/alertum"

    suspend fun getByUser(onlyUnread: Boolean = false, page: Int = 1, pageSize: Int = 20): Result<List<AlertDto>> {
        return try {
            val alerts = client.get("$baseUrl/user/$userId") {
                url {
                    parameters.append("onlyUnread", onlyUnread.toString())
                    parameters.append("page", page.toString())
                    parameters.append("pageSize", pageSize.toString())
                }
            }.body<List<AlertDto>>()
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(): Result<UnreadCountResponse> {
        return try {
            val count = client.get("$baseUrl/user/$userId/unread/count").body<UnreadCountResponse>()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(alertId: Int): Result<Unit> {
        return try {
            client.post("$baseUrl/$alertId/mark-read") {
                url {
                    parameters.append("userId", userId.toString())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAlert(alert: AlertDto): Result<AlertDto> {
        return try {
            val newAlert = client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(alert)
            }.body<AlertDto>()
            Result.success(newAlert)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAll(): Result<List<AlertDto>> {
        return try {
            val alerts = client.get(baseUrl).body<List<AlertDto>>()
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getById(id: Int): Result<AlertDto> {
        return try {
            val alert = client.get("$baseUrl/$id").body<AlertDto>()
            Result.success(alert)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

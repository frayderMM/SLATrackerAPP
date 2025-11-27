package dev.esandamzapp.slatrackerapp.data.remote

import dev.esandamzapp.slatrackerapp.data.remote.dto.AlertDto
import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginRequest
import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginResponse
import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse
import dev.esandamzapp.slatrackerapp.data.remote.dto.UnreadCountResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ===============================
    // == Endpoints de Autenticación
    // ===============================
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // ===============================
    // == Endpoints de Perfil
    // ===============================
    @GET("api/perfil/{userId}/completo")
    suspend fun getPerfilCompleto(@Path("userId") userId: Int): PerfilCompletoResponse

    // ===============================
    // == Endpoints de Alertas
    // ===============================

    @GET("api/alertum/user/{userId}")
    suspend fun getAlertsByUser(
        @Path("userId") userId: Int,
        @Query("onlyUnread") onlyUnread: Boolean = true,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<AlertDto>

    @GET("api/alertum/user/{userId}/unread/count")
    suspend fun getUnreadCount(@Path("userId") userId: Int): UnreadCountResponse

    @POST("api/alertum/{id}/mark-read")
    suspend fun markAsRead(
        @Path("id") alertId: Int,
        @Query("userId") userId: Int
    ): Response<Unit> // Usamos Response<Unit> para manejar respuestas sin cuerpo (204 No Content)

}

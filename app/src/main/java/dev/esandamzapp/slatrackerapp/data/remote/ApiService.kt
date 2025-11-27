package dev.esandamzapp.slatrackerapp.data.remote

import dev.esandamzapp.slatrackerapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/Usuarios/authenticate")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("/api/Usuarios/{id}/perfil-completo")
    suspend fun getPerfilCompleto(
        @Path("id") idUsuario: Int,
        @Header("Authorization") token: String
    ): Response<PerfilCompletoResponse>

    // Dashboard Endpoints
    @GET("/api/Dashboard/sla/data")
    suspend fun getDashboardData(
        @Query("slaCode") slaCode: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("bloqueTech") bloqueTech: String? = null
    ): Response<List<DashboardSlaDto>>

    @GET("/api/Dashboard/sla/statistics")
    suspend fun getDashboardStatistics(): Response<DashboardStatsDto>

    @GET("/api/ConfigSla")
    suspend fun getConfigSla(): Response<List<ConfigSla>>
}

package dev.esandamzapp.slatrackerapp.data.remote

import dev.esandamzapp.slatrackerapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/Usuarios/authenticate")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("/api/Usuarios/{id}")
    suspend fun getUsuario(
        @Path("id") idUsuario: Int
    ): Response<UsuarioDto>

    // Dashboard Endpoints
    @GET("/api/Dashboard/sla/data")
    suspend fun getDashboardData(
        @Query("slaCode") slaCode: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("bloqueTech") bloqueTech: String? = null
    ): Response<DashboardDataResponse>

    @GET("/api/Dashboard/sla/statistics")
    suspend fun getDashboardStatistics(): Response<DashboardStatsResponse>

    @GET("/api/ConfigSla")
    suspend fun getConfigSla(): Response<List<ConfigSla>>

    @GET("/api/RolRegistro")
    suspend fun getRolesRegistro(): Response<List<RolRegistroDto>>
}

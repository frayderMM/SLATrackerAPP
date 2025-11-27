package dev.esandamzapp.slatrackerapp.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

/**
 * Interfaz que define los endpoints de tu API .NET
 */
interface SlaApiService {

    // ========================================================================
    // --- DASHBOARD ---
    // ========================================================================

    // Endpoint: /api/dashboard/sla/statistics
    // Respuesta: { "data": { "totalSolicitudes": 150, ... } }
    @GET("api/dashboard/sla/statistics")
    suspend fun getSlaStatistics(): ApiResponse<SlaStatisticsDto>

    // Endpoint: /api/dashboard/sla/data
    // Respuesta: { "data": [ { "id": 1, "bloqueTech": "Infraestructura", ... } ] }
    @GET("api/dashboard/sla/data")
    suspend fun getSlaData(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("bloqueTech") bloqueTech: String?,
        @Query("tipoSolicitud") tipoSolicitud: String?,
        @Query("prioridad") prioridad: String?,
        @Query("cumpleSla") cumpleSla: Boolean?
    ): ApiResponse<List<SlaRequestDto>>

    // Endpoint: /api/dashboard/filters
    @GET("api/dashboard/filters")
    suspend fun getDashboardFilters(): ApiResponse<DashboardFiltersDto>


    // ========================================================================
    // --- CONFIGURACIONES (Catálogos) ---
    // ========================================================================

    // Áreas
    @GET("api/areas")
    suspend fun getAreas(): List<AreaDto>

    @POST("api/areas")
    suspend fun createArea(@Body area: AreaDto)

    @DELETE("api/areas/{id}")
    suspend fun deleteArea(@Path("id") id: Int)

    // Tipos de Solicitud
    @GET("api/tiposolicitudcatalogo")
    suspend fun getRequestTypes(): List<RequestTypeDto>

    @POST("api/tiposolicitudcatalogo")
    suspend fun createRequestType(@Body type: RequestTypeDto)

    @DELETE("api/tiposolicitudcatalogo/{id}")
    suspend fun deleteRequestType(@Path("id") id: Int)

    // Prioridades
    @GET("api/prioridadcatalogo")
    suspend fun getPriorities(): List<PriorityDto>

    @POST("api/prioridadcatalogo")
    suspend fun createPriority(@Body priority: PriorityDto)

    @DELETE("api/prioridadcatalogo/{id}")
    suspend fun deletePriority(@Path("id") id: Int)
}

// ========================================================================
// --- DTOs (Modelos de Datos basados en tus JSON) ---
// ========================================================================

// Wrapper genérico para respuestas que vienen dentro de "data": { ... }
data class ApiResponse<T>(
    val data: T
)

// 1. DTO para /api/dashboard/sla/data
data class SlaRequestDto(
    val id: Int,
    val bloqueTech: String,
    val tipoSolicitud: String,
    val prioridad: String,
    val fechaSolicitud: String, // ISO String "2025-11-20T10:30:00"
    val fechaIngreso: String?,
    val diasTranscurridos: Int,
    val cumpleSla: Boolean,
    val cumpleSla1: Boolean,
    val cumpleSla2: Boolean,
    val nombrePersonal: String?,
    val diasUmbralSla: Int
)

// 2. DTO para /api/dashboard/sla/statistics
data class SlaStatisticsDto(
    val totalSolicitudes: Int,
    val estadisticasPorTipo: Map<String, StatDetailDto>?,
    val cumplimientoSla1: Double,
    val cumplimientoSla2: Double,
    val promedioDiasSla1: Double,
    val promedioDiasSla2: Double
)

data class StatDetailDto(
    val totalSolicitudes: Int,
    val cumpleSla: Int,
    val porcentajeCumplimiento: Double,
    val promedioDias: Double,
    val diasUmbral: Int
)

// 3. DTO para /api/dashboard/filters
data class DashboardFiltersDto(
    val bloquesTech: List<String>,
    val tiposSolicitud: List<String>,
    val prioridades: List<String>,
    val configuracionesSla: List<SlaConfigSimpleDto>
)

data class SlaConfigSimpleDto(
    val id: Int,
    val codigoSla: String,
    val tipoSolicitud: String,
    val diasUmbral: Int
)

// --- DTOs de Configuración (Tablas Catálogo) ---

data class RequestTypeDto(
    @SerializedName("idTipoSolicitud") val id: Int? = null,
    val codigo: String,
    val descripcion: String,
    val activo: Boolean
)

data class PriorityDto(
    @SerializedName("idPrioridad") val id: Int? = null,
    val codigo: String,
    val descripcion: String,
    val nivel: Int,
    val slaMultiplier: Double,
    val icon: String?,
    val color: String?,
    val activo: Boolean
)

data class AreaDto(
    // Asumiendo estructura basada en los otros catálogos, ajusta si varía
    val id: Int? = null,
    val nombre: String
)
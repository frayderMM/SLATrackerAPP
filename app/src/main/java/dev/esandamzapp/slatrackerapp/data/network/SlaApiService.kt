package dev.esandamzapp.slatrackerapp.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

/**
 * Interfaz principal para las peticiones a la API .NET
 * Incluye todos los DTOs necesarios en un solo archivo para evitar errores de referencia.
 */
interface SlaApiService {

    // ========================================================================
    // --- DASHBOARD (Home) ---
    // ========================================================================

    // 1. Estadísticas Generales
    @GET("api/dashboard/sla/statistics")
    suspend fun getSlaStatistics(): ApiResponse<SlaStatisticsDto>

    // 2. Datos Filtrados (Lista principal)
    @GET("api/dashboard/sla/data")
    suspend fun getSlaData(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("bloqueTech") bloqueTech: String?,
        @Query("tipoSolicitud") tipoSolicitud: String?,
        @Query("prioridad") prioridad: String?,
        @Query("cumpleSla") cumpleSla: Boolean?
    ): ApiResponse<List<SlaRequestDto>>

    // 3. Filtros Disponibles (Catálogos)
    @GET("api/dashboard/filters")
    suspend fun getDashboardFilters(): ApiResponse<DashboardFiltersDto>


    // ========================================================================
    // --- CONFIGURACIONES (Pantallas de Admin) ---
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
// --- MODELOS DE DATOS (DTOs) BLINDADOS ---
// Usamos 'alternate' para soportar tanto PascalCase (C#) como camelCase
// ========================================================================

// Wrapper genérico { "data": ... }
data class ApiResponse<T>(
    @SerializedName("data", alternate = ["Data"])
    val data: T
)

// DTO Principal: Solicitud SLA
data class SlaRequestDto(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,

    @SerializedName("bloqueTech", alternate = ["BloqueTech", "area", "Area"])
    val bloqueTech: String?,

    @SerializedName("tipoSolicitud", alternate = ["TipoSolicitud"])
    val tipoSolicitud: String?,

    @SerializedName("prioridad", alternate = ["Prioridad"])
    val prioridad: String?,

    @SerializedName("fechaSolicitud", alternate = ["FechaSolicitud"])
    val fechaSolicitud: String?,

    @SerializedName("fechaIngreso", alternate = ["FechaIngreso"])
    val fechaIngreso: String?,

    @SerializedName("diasTranscurridos", alternate = ["DiasTranscurridos"])
    val diasTranscurridos: Int,

    @SerializedName("cumpleSla", alternate = ["CumpleSla"])
    val cumpleSla: Boolean,

    @SerializedName("cumpleSla1", alternate = ["CumpleSla1"])
    val cumpleSla1: Boolean,

    @SerializedName("cumpleSla2", alternate = ["CumpleSla2"])
    val cumpleSla2: Boolean,

    @SerializedName("nombrePersonal", alternate = ["NombrePersonal", "personal"])
    val nombrePersonal: String?,

    @SerializedName("diasUmbralSla", alternate = ["DiasUmbralSla", "diasUmbral"])
    val diasUmbralSla: Int
)

// DTO Filtros
data class DashboardFiltersDto(
    @SerializedName("bloquesTech", alternate = ["BloquesTech"])
    val bloquesTech: List<String>,

    @SerializedName("tiposSolicitud", alternate = ["TiposSolicitud"])
    val tiposSolicitud: List<String>,

    @SerializedName("prioridades", alternate = ["Prioridades"])
    val prioridades: List<String>
)

// DTO Estadísticas (Statistics)
data class SlaStatisticsDto(
    @SerializedName("totalSolicitudes", alternate = ["TotalSolicitudes"])
    val totalSolicitudes: Int,

    @SerializedName("cumplimientoSla1", alternate = ["CumplimientoSla1"])
    val cumplimientoSla1: Double,

    @SerializedName("cumplimientoSla2", alternate = ["CumplimientoSla2"])
    val cumplimientoSla2: Double
)

data class StatDetailDto(
    val totalSolicitudes: Int,
    val cumpleSla: Int,
    val porcentajeCumplimiento: Double
)

// DTOs Configuración
data class AreaDto(
    val id: Int? = null,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true
)

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
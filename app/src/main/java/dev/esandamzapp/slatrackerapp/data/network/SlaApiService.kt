package dev.esandamzapp.slatrackerapp.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Interfaz principal para las peticiones a la API .NET.
 * CORRECCIÓN: Mapeo de "areas" en filtros y DTOs actualizados.
 */
interface SlaApiService {

    // ========================================================================
    // --- DASHBOARD (/api/Dashboard) ---
    // ========================================================================

    @GET("api/Dashboard/sla/statistics")
    suspend fun getSlaStatistics(): ApiResponse<SlaStatisticsDto>

    @GET("api/Dashboard/sla/data")
    suspend fun getSlaData(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("area") bloqueTech: List<String>?,       // Mapeado a "area" en la URL
        @Query("tipoSolicitud") tipoSolicitud: List<String>?,
        @Query("prioridad") prioridad: List<String>?,
        @Query("cumpleSla") cumpleSla: Boolean?
    ): ApiResponse<List<SlaRequestDto>>

    @GET("api/Dashboard/filters")
    suspend fun getDashboardFilters(): ApiResponse<DashboardFiltersDto>


    // ========================================================================
    // --- CONFIGURACIÓN SLA (/api/ConfigSla) ---
    // ========================================================================

    @GET("api/ConfigSla")
    suspend fun getConfigSlas(): List<SlaConfigDto>

    @GET("api/ConfigSla/{id}")
    suspend fun getConfigSlaById(@Path("id") id: Int): SlaConfigDto

    @POST("api/ConfigSla")
    suspend fun createConfigSla(@Body config: SlaConfigDto): SlaConfigDto

    @PUT("api/ConfigSla/{id}")
    suspend fun updateConfigSla(@Path("id") id: Int, @Body config: SlaConfigDto): SlaConfigDto

    @DELETE("api/ConfigSla/{id}")
    suspend fun deleteConfigSla(@Path("id") id: Int)


    // ========================================================================
    // --- ÁREAS (/api/Areas) ---
    // ========================================================================

    @GET("api/Areas")
    suspend fun getAreas(): List<AreaDto>

    @GET("api/Areas/{id}")
    suspend fun getAreaById(@Path("id") id: Int): AreaDto

    @POST("api/Areas")
    suspend fun createArea(@Body area: AreaDto): AreaDto

    @PUT("api/Areas/{id}")
    suspend fun updateArea(@Path("id") id: Int, @Body area: AreaDto): AreaDto

    @DELETE("api/Areas/{id}")
    suspend fun deleteArea(@Path("id") id: Int)


    // ========================================================================
    // --- TIPOS DE SOLICITUD (/api/TipoSolicitudCatalogo) ---
    // ========================================================================

    @GET("api/TipoSolicitudCatalogo")
    suspend fun getRequestTypes(): List<RequestTypeDto>

    @GET("api/TipoSolicitudCatalogo/{id}")
    suspend fun getRequestTypeById(@Path("id") id: Int): RequestTypeDto

    @POST("api/TipoSolicitudCatalogo")
    suspend fun createRequestType(@Body type: RequestTypeDto): RequestTypeDto

    @PUT("api/TipoSolicitudCatalogo/{id}")
    suspend fun updateRequestType(@Path("id") id: Int, @Body type: RequestTypeDto): RequestTypeDto

    @DELETE("api/TipoSolicitudCatalogo/{id}")
    suspend fun deleteRequestType(@Path("id") id: Int)


    // ========================================================================
    // --- PRIORIDADES (/api/PrioridadCatalogo) ---
    // ========================================================================

    @GET("api/PrioridadCatalogo")
    suspend fun getPriorities(): List<PriorityDto>

    @GET("api/PrioridadCatalogo/{id}")
    suspend fun getPriorityById(@Path("id") id: Int): PriorityDto

    @POST("api/PrioridadCatalogo")
    suspend fun createPriority(@Body priority: PriorityDto): PriorityDto

    @PUT("api/PrioridadCatalogo/{id}")
    suspend fun updatePriority(@Path("id") id: Int, @Body priority: PriorityDto): PriorityDto

    @DELETE("api/PrioridadCatalogo/{id}")
    suspend fun deletePriority(@Path("id") id: Int)


    // ========================================================================
    // --- REPORTES (/api/Reporte) ---
    // ========================================================================

    @GET("api/Reporte/dashboard-data")
    suspend fun getReportData(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): ApiResponse<List<SlaRequestDto>>

    @GET("api/Reporte")
    suspend fun getReportHistory(): List<ReporteDto>

    @GET("api/Reporte/{id}")
    suspend fun getReportById(@Path("id") id: Int): ReporteDto

    @POST("api/Reporte")
    suspend fun createReport(@Body reporte: ReporteDto): ReporteDto

    @DELETE("api/Reporte/{id}")
    suspend fun deleteReport(@Path("id") id: Int)

    @Multipart
    @POST("api/Reporte/upload")
    suspend fun uploadReport(
        @Part file: MultipartBody.Part,
        @Part("description") description: okhttp3.RequestBody? = null
    ): ResponseBody
}

// ========================================================================
// --- MODELOS DE DATOS (DTOs) ---
// ========================================================================

data class ApiResponse<T>(
    @SerializedName("data", alternate = ["Data"])
    val data: T
)

// DTO Solicitud
data class SlaRequestDto(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,
    // IMPORTANTE: Soporta "bloqueTech", "area" y "Area"
    @SerializedName("bloqueTech", alternate = ["BloqueTech", "area", "Area", "nombreArea"])
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

// DTO Filtros - CORREGIDO AQUÍ
data class DashboardFiltersDto(
    // AHORA INCLUYE "areas" y "Areas" como nombres alternativos
    @SerializedName("bloquesTech", alternate = ["BloquesTech", "areas", "Areas"])
    val bloquesTech: List<String>?,

    @SerializedName("tiposSolicitud", alternate = ["TiposSolicitud"])
    val tiposSolicitud: List<String>?,

    @SerializedName("prioridades", alternate = ["Prioridades"])
    val prioridades: List<String>?
)

data class SlaStatisticsDto(
    @SerializedName("totalSolicitudes", alternate = ["TotalSolicitudes"])
    val totalSolicitudes: Int,
    @SerializedName("cumplimientoSla1", alternate = ["CumplimientoSla1"])
    val cumplimientoSla1: Double,
    @SerializedName("cumplimientoSla2", alternate = ["CumplimientoSla2"])
    val cumplimientoSla2: Double
)

// Otros DTOs
data class AreaDto(
    @SerializedName("idArea") val id: Int? = null, // Ajustado al JSON que me mostraste
    @SerializedName("nombreArea") val nombre: String,
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

data class SlaConfigDto(
    val id: Int? = null,
    val codigoSla: String,
    val descripcion: String?,
    val diasUmbral: Int,
    val esActivo: Boolean,
    val idTipoSolicitud: Int
)

data class ReporteDto(
    val id: Int? = null,
    val nombreReporte: String,
    val fechaGeneracion: String?,
    val urlArchivo: String?,
    val usuarioGenerador: String?
)
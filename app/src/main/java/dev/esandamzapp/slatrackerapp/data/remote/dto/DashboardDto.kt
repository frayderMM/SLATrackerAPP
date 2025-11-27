package dev.esandamzapp.slatrackerapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// Response del endpoint /api/Dashboard/sla/data
data class DashboardSlaDto(
    @SerializedName("idSolicitud") val idSolicitud: Int,
    @SerializedName("bloqueTech") val bloqueTech: String?,
    @SerializedName("tipoSolicitud") val tipoSolicitud: String?,
    @SerializedName("diasTranscurridos") val diasTranscurridos: Int,
    @SerializedName("cumpleSla1") val cumpleSla1: Boolean?,
    @SerializedName("cumpleSla2") val cumpleSla2: Boolean?,
    @SerializedName("sla1Dias") val sla1Dias: Int?,
    @SerializedName("sla2Dias") val sla2Dias: Int?,
    @SerializedName("porcentajeCompletadoSla1") val porcentajeCompletadoSla1: Double?,
    @SerializedName("porcentajeCompletadoSla2") val porcentajeCompletadoSla2: Double?,
    @SerializedName("estadoSolicitud") val estadoSolicitud: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?
)

// Response del endpoint /api/Dashboard/sla/statistics
data class DashboardStatsDto(
    @SerializedName("totalSolicitudes") val totalSolicitudes: Int,
    @SerializedName("cumplimientoSla1") val cumplimientoSla1: Double,
    @SerializedName("cumplimientoSla2") val cumplimientoSla2: Double,
    @SerializedName("tiempoPromedio") val tiempoPromedio: Double
)

// Response del endpoint /api/ConfigSla
data class ConfigSla(
    @SerializedName("idConfigSla") val idConfigSla: Int,
    @SerializedName("codigoSla") val codigoSla: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("diasUmbral") val diasUmbral: Int
)

package dev.esandamzapp.slatrackerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlertDto(
    val idAlerta: Int,
    val idSolicitud: Int,
    val idTipoAlerta: Int,
    val idEstadoAlerta: Int,
    val nivel: String?,
    val mensaje: String?,
    val enviadoEmail: Boolean?,
    val fechaCreacion: String?,
    val fechaLectura: String?,
    val actualizadoEn: String?
)

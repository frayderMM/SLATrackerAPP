package dev.esandamzapp.slatrackerapp.data.remote

data class SlaRequest(
    val idPersonal: Int,
    val idRolRegistro: Int,
    val idSla: Int,
    val idArea: Int,
    val idEstadoSolicitud: Int,
    val fechaSolicitud: String,
    val fechaIngreso: String,
    val numDiasSla: Int,
    val resumenSla: String,
    val origenDato: String,
    val creadoPor: Int
)

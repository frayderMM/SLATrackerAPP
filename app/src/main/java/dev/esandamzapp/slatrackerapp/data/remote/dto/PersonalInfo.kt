package dev.esandamzapp.slatrackerapp.data.remote.dto

data class PersonalInfo(
    val idPersonal: Int,
    val idUsuario: Int,
    val nombres: String,
    val apellidos: String,
    val documento: String,
    val estado: String,
    val creadoEn: String,
    val actualizadoEn: String?
)

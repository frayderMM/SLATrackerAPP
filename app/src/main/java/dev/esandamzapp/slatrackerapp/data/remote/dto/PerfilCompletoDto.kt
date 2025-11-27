package dev.esandamzapp.slatrackerapp.data.remote.dto

// Este DTO representa la estructura completa del perfil del usuario que viene del backend.
data class PerfilCompletoDto(
    val usuario: UsuarioProfileDto,
    val estadisticas: EstadisticasDto
)

// Sub-DTO para la información del usuario.
data class UsuarioProfileDto(
    val nombreCompleto: String,
    val correo: String,
    val departamento: String,
    val rol: String
)

// Sub-DTO para las estadísticas del usuario.
data class EstadisticasDto(
    val totalSolicitudes: Int,
    val cumplidas: Int,
    val incumplidas: Int
)

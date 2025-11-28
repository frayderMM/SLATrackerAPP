package dev.esandamzapp.slatrackerapp.data.remote.dto

data class LoginResponse(
    val token: String
)

data class UsuarioProfile(
    val idUsuario: Int,
    val username: String,
    val correo: String,
    val idRolSistema: Int,
    val idEstadoUsuario: Int
)

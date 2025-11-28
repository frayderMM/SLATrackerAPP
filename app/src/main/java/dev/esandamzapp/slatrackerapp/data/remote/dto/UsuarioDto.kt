package dev.esandamzapp.slatrackerapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("username") val username: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("idRolSistema") val idRolSistema: Int,
    @SerializedName("idEstadoUsuario") val idEstadoUsuario: Int
)

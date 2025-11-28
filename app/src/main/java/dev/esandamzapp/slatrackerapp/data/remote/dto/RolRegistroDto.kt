package dev.esandamzapp.slatrackerapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RolRegistroDto(
    @SerializedName("idRolRegistro") val idRolRegistro: Int,
    @SerializedName("bloqueTech") val bloqueTech: String?,
    @SerializedName("nombreRol") val nombreRol: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("esActivo") val esActivo: Boolean?
)

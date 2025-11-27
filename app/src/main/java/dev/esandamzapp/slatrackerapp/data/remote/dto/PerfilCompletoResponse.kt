package dev.esandamzapp.slatrackerapp.data.remote.dto

data class PerfilCompletoResponse(
    val usuario: UsuarioProfile,
    val personal: PersonalData?,
    val rol: RolData?,
    val area: AreaData?
)

data class PersonalData(
    val idPersonal: Int,
    val idUsuario: Int,
    val nombres: String,
    val apellidos: String,
    val documento: String,
    val estado: String
)

data class RolData(
    val idRolSistema: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val esActivo: Boolean
)

data class AreaData(
    val idArea: Int?,
    val nombre: String?
)

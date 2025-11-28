package dev.esandamzapp.slatrackerapp.ui.profile

import dev.esandamzapp.slatrackerapp.data.remote.dto.UsuarioDto

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val usuario: UsuarioDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

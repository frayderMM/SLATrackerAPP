package dev.esandamzapp.slatrackerapp.ui.profile

import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val perfil: PerfilCompletoResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

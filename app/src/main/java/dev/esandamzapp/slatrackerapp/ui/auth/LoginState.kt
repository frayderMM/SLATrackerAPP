package dev.esandamzapp.slatrackerapp.ui.auth

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val userId: Int) : LoginState()
    data class Error(val message: String) : LoginState()
}


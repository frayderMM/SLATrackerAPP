package dev.esandamzapp.slatrackerapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        // Inicializar ApiClient con contexto
        ApiClient.initialize(application)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                val res = repository.login(username, password)

                // Extraer userId del token JWT
                val userId = extractUserIdFromToken(res.token)

                // Guardar token en SharedPreferences
                ApiClient.getTokenManager()?.saveToken(res.token)
                ApiClient.getTokenManager()?.saveUserId(userId)

                _loginState.value = LoginState.Success(
                    token = res.token,
                    userId = userId
                )

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Credenciales incorrectas: ${e.message}")
            }
        }
    }

    private fun extractUserIdFromToken(token: String): Int {
        try {
            // Decodificar el payload del JWT (segunda parte del token)
            val parts = token.split(".")
            if (parts.size != 3) return 0
            
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
            
            // Extraer el campo "UserId" del JSON
            val userIdMatch = "\"UserId\":\"(\\d+)\"".toRegex().find(payload)
            return userIdMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        } catch (e: Exception) {
            return 0
        }
    }
}

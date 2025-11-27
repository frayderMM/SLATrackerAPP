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

                // Guardar token en SharedPreferences
                ApiClient.getTokenManager()?.saveToken(res.token)
                ApiClient.getTokenManager()?.saveUserId(res.usuario.idUsuario)

                _loginState.value = LoginState.Success(
                    token = res.token,
                    userId = res.usuario.idUsuario
                )

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Credenciales incorrectas: ${e.message}")
            }
        }
    }
}

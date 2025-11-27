package dev.esandamzapp.slatrackerapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                val res = repository.login(username, password)

                // Corregido: El userId viene directamente en la respuesta, no en un sub-objeto.
                _loginState.value = LoginState.Success(
                    token = res.token,
                    userId = res.userId
                )


            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Credenciales incorrectas")
            }
        }
    }
}

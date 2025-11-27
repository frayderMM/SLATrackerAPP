package dev.esandamzapp.slatrackerapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


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

            } catch (e: HttpException) {
                // Error HTTP (401, 403, 500, etc.)
                val errorMessage = when (e.code()) {
                    401 -> "Credenciales incorrectas"
                    403 -> "Acceso denegado"
                    404 -> "Servicio no encontrado"
                    500 -> "Error del servidor. Inténtalo más tarde"
                    else -> "Error HTTP ${e.code()}: ${e.message()}"
                }
                Log.e("LoginViewModel", "Error HTTP ${e.code()}: ${e.message()}", e)
                _loginState.value = LoginState.Error(errorMessage)
            } catch (e: IOException) {
                // Error de conexión
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "No se puede conectar al servidor. Verifica que el backend esté corriendo en http://10.0.2.2:5192"
                    e.message?.contains("Connection refused") == true -> 
                        "Conexión rechazada. El servidor no está respondiendo. Verifica que el backend esté corriendo."
                    e.message?.contains("timeout") == true -> 
                        "Timeout de conexión. El servidor tardó demasiado en responder."
                    else -> "Sin conexión al servidor: ${e.message ?: "Error desconocido"}. Verifica:\n1. Que el backend esté corriendo\n2. Que el puerto sea 5192\n3. Que no haya firewall bloqueando"
                }
                Log.e("LoginViewModel", "Error de conexión: ${e.message}", e)
                Log.e("LoginViewModel", "URL intentada: http://10.0.2.2:5192/api/auth/login")
                _loginState.value = LoginState.Error(errorMessage)
            } catch (e: Exception) {
                // Otros errores (JSON parsing, etc.)
                Log.e("LoginViewModel", "Error desconocido", e)
                _loginState.value = LoginState.Error("Error: ${e.message ?: "Error desconocido"}")
            }
        }
    }
}

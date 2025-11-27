package dev.esandamzapp.slatrackerapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.repository.ProfileRepository
import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    fun loadProfile(token: String, userId: Int) {
        viewModelScope.launch {
            try {
                _state.value = ProfileState.Loading
                val data = repository.getPerfilCompleto(token, userId)
                _state.value = ProfileState.Success(data)
            } catch (e: HttpException) {
                _state.value = ProfileState.Error("Error HTTP ${e.code()}")
            } catch (e: IOException) {
                _state.value = ProfileState.Error("Sin conexión al servidor")
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Error desconocido: ${e.message}")
            }
        }
    }
}

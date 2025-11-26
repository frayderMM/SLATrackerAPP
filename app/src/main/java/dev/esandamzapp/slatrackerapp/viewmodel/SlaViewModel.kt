package dev.esandamzapp.slatrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.remote.SlaRequest
import dev.esandamzapp.slatrackerapp.data.repository.SlaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SlaViewModel : ViewModel() {

    private val repository = SlaRepository()

    private val _uiState = MutableStateFlow<SlaUiState>(SlaUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createSla(request: SlaRequest) {
        viewModelScope.launch {
            _uiState.value = SlaUiState.Loading
            val result = repository.createSla(request)
            _uiState.value = result.fold(
                onSuccess = { SlaUiState.Success },
                onFailure = { exception ->
                    SlaUiState.Error(exception.message ?: "No se pudo crear la solicitud.")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = SlaUiState.Idle
    }

}

sealed class SlaUiState {
    object Idle : SlaUiState()
    object Loading : SlaUiState()
    object Success : SlaUiState()
    data class Error(val message: String) : SlaUiState()
}

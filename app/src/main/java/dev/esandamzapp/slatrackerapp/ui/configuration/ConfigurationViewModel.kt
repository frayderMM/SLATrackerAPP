package dev.esandamzapp.slatrackerapp.ui.configuration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Modelo combinado para la UI (Tipo + Días)
data class SlaTypeUiItem(
    val idType: Int,
    val nombre: String,  // Descripción del tipo
    val codigo: String,
    val dias: Int,       // Viene de la tabla ConfigSla
    val idConfig: Int?   // ID de la regla en ConfigSla (para updates)
)

class ConfigurationViewModel : ViewModel() {

    // --- Estados de Datos ---

    // Lista unificada para la UI
    private val _slaUiItems = MutableStateFlow<List<SlaTypeUiItem>>(emptyList())
    val slaUiItems = _slaUiItems.asStateFlow()

    private val _priorities = MutableStateFlow<List<PriorityDto>>(emptyList())
    val priorities = _priorities.asStateFlow()

    private val _roles = MutableStateFlow<List<AreaDto>>(emptyList())
    val roles = _roles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadAllConfigurations()
    }

    fun loadAllConfigurations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Carga en paralelo
                val areasDeferred = withContext(Dispatchers.IO) { RetrofitClient.api.getAreas() }
                val typesDeferred = withContext(Dispatchers.IO) { RetrofitClient.api.getRequestTypes() }
                val configsDeferred = withContext(Dispatchers.IO) { RetrofitClient.api.getConfigSlas() }
                val prioritiesDeferred = withContext(Dispatchers.IO) { RetrofitClient.api.getPriorities() }

                _roles.value = areasDeferred
                _priorities.value = prioritiesDeferred

                // Combinar Tipos + Configuración SLA en una sola lista UI
                _slaUiItems.value = typesDeferred.map { type ->
                    // Buscamos la regla de SLA que corresponda a este tipo
                    val config = configsDeferred.find { it.idTipoSolicitud == type.id }
                    SlaTypeUiItem(
                        idType = type.id ?: 0,
                        nombre = type.descripcion,
                        codigo = type.codigo,
                        dias = config?.diasUmbral ?: 0, // Si no tiene regla, 0 días
                        idConfig = config?.id
                    )
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error cargando configuración: ${e.message}"
                Log.e("ConfigViewModel", "Error loadAll: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- ÁREAS (ROLES) ---
    fun addRole(nombre: String, descripcion: String) {
        viewModelScope.launch {
            try {
                val newArea = AreaDto(nombre = nombre, descripcion = descripcion, activo = true)
                withContext(Dispatchers.IO) { RetrofitClient.api.createArea(newArea) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }
    fun updateRole(id: Int, nombre: String, descripcion: String) {
        viewModelScope.launch {
            try {
                val area = AreaDto(id = id, nombre = nombre, descripcion = descripcion, activo = true)
                withContext(Dispatchers.IO) { RetrofitClient.api.updateArea(id, area) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }
    fun deleteRole(id: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { RetrofitClient.api.deleteArea(id) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }

    // --- TIPOS DE SOLICITUD + SLA (Gestión Unificada) ---

    fun addSlaTypeUnificado(nombre: String, codigo: String, dias: Int) {
        viewModelScope.launch {
            try {
                // 1. Crear el Tipo de Solicitud
                val newType = RequestTypeDto(codigo = codigo.uppercase(), descripcion = nombre, activo = true)
                val createdType = withContext(Dispatchers.IO) { RetrofitClient.api.createRequestType(newType) }

                // 2. Crear la Regla de SLA asociada automáticamente
                if (createdType.id != null) {
                    val newConfig = SlaConfigDto(
                        codigoSla = "SLA_${codigo.uppercase()}",
                        descripcion = "Regla para $nombre",
                        diasUmbral = dias,
                        esActivo = true,
                        idTipoSolicitud = createdType.id
                    )
                    withContext(Dispatchers.IO) { RetrofitClient.api.createConfigSla(newConfig) }
                }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error creando SLA: ${e.message}" }
        }
    }

    fun updateSlaTypeUnificado(uiItem: SlaTypeUiItem, nombre: String, codigo: String, dias: Int) {
        viewModelScope.launch {
            try {
                // 1. Actualizar el Tipo
                val typeDto = RequestTypeDto(id = uiItem.idType, codigo = codigo.uppercase(), descripcion = nombre, activo = true)
                withContext(Dispatchers.IO) { RetrofitClient.api.updateRequestType(uiItem.idType, typeDto) }

                // 2. Actualizar (o crear si faltaba) la Regla SLA
                if (uiItem.idConfig != null) {
                    val configDto = SlaConfigDto(
                        id = uiItem.idConfig,
                        codigoSla = "SLA_${codigo.uppercase()}",
                        descripcion = "Regla para $nombre",
                        diasUmbral = dias,
                        esActivo = true,
                        idTipoSolicitud = uiItem.idType
                    )
                    withContext(Dispatchers.IO) { RetrofitClient.api.updateConfigSla(uiItem.idConfig, configDto) }
                } else {
                    // Caso raro: existía el tipo pero no la regla. La creamos.
                    val newConfig = SlaConfigDto(
                        codigoSla = "SLA_${codigo.uppercase()}",
                        descripcion = "Regla para $nombre",
                        diasUmbral = dias,
                        esActivo = true,
                        idTipoSolicitud = uiItem.idType
                    )
                    withContext(Dispatchers.IO) { RetrofitClient.api.createConfigSla(newConfig) }
                }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error actualizando SLA: ${e.message}" }
        }
    }

    fun deleteSlaTypeUnificado(uiItem: SlaTypeUiItem) {
        viewModelScope.launch {
            try {
                // Borramos la configuración primero (por integridad referencial) si existe
                if (uiItem.idConfig != null) {
                    withContext(Dispatchers.IO) { RetrofitClient.api.deleteConfigSla(uiItem.idConfig) }
                }
                // Luego borramos el tipo
                withContext(Dispatchers.IO) { RetrofitClient.api.deleteRequestType(uiItem.idType) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error eliminando SLA: ${e.message}" }
        }
    }

    // --- PRIORIDADES ---
    fun addPriority(nombre: String, nivel: Int, slaMultiplier: Double) {
        viewModelScope.launch {
            try {
                val newPrio = PriorityDto(codigo = nombre.uppercase(), descripcion = nombre, nivel = nivel, slaMultiplier = slaMultiplier, icon = "icon", color = "#000000", activo = true)
                withContext(Dispatchers.IO) { RetrofitClient.api.createPriority(newPrio) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }
    fun updatePriority(id: Int, nombre: String, nivel: Int, slaMultiplier: Double) {
        viewModelScope.launch {
            try {
                val prio = PriorityDto(id = id, codigo = nombre.uppercase(), descripcion = nombre, nivel = nivel, slaMultiplier = slaMultiplier, icon = "icon", color = "#000000", activo = true)
                withContext(Dispatchers.IO) { RetrofitClient.api.updatePriority(id, prio) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }
    fun deletePriority(id: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { RetrofitClient.api.deletePriority(id) }
                loadAllConfigurations()
            } catch (e: Exception) { _errorMessage.value = "Error: ${e.message}" }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
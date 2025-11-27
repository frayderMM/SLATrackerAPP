package dev.esandamzapp.slatrackerapp.ui.configuration

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfigurationViewModel : ViewModel() {

    // --- Tipos de SLA ---
    private val _slaTypes = MutableStateFlow(listOf("SLA1", "SLA2"))
    val slaTypes = _slaTypes.asStateFlow()

    fun addSlaType(newType: String) {
        if (newType.isNotBlank() && !_slaTypes.value.contains(newType)) {
            _slaTypes.value = _slaTypes.value + newType
        }
    }

    fun updateSlaType(oldType: String, newType: String) {
        if (newType.isNotBlank()) {
            _slaTypes.value = _slaTypes.value.map { if (it == oldType) newType else it }
        }
    }

    fun deleteSlaType(typeToDelete: String) {
        _slaTypes.value = _slaTypes.value - typeToDelete
    }

    // --- Prioridades ---
    private val _priorities = MutableStateFlow(listOf("Crítica", "Alta", "Media", "Baja"))
    val priorities = _priorities.asStateFlow()

    fun addPriority(newPriority: String) {
        if (newPriority.isNotBlank() && !_priorities.value.contains(newPriority)) {
            _priorities.value = _priorities.value + newPriority
        }
    }

    fun updatePriority(oldPriority: String, newPriority: String) {
        if (newPriority.isNotBlank()) {
            _priorities.value = _priorities.value.map { if (it == oldPriority) newPriority else it }
        }
    }

    fun deletePriority(priorityToDelete: String) {
        _priorities.value = _priorities.value - priorityToDelete
    }

    // --- Roles / Bloques Tecnológicos ---
    private val _roles = MutableStateFlow(listOf("Frontend", "Backend", "Data Science", "RRHH", "Diseño"))
    val roles = _roles.asStateFlow()

    fun addRole(newRole: String) {
        if (newRole.isNotBlank() && !_roles.value.contains(newRole)) {
            _roles.value = _roles.value + newRole
        }
    }

    fun updateRole(oldRole: String, newRole: String) {
        if (newRole.isNotBlank()) {
            _roles.value = _roles.value.map { if (it == oldRole) newRole else it }
        }
    }

    fun deleteRole(roleToDelete: String) {
        _roles.value = _roles.value - roleToDelete
    }
}
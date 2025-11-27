package dev.esandamzapp.slatrackerapp.ui.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ConfigurationScreen(navController: NavController, configViewModel: ConfigurationViewModel = viewModel()) {
    var selectedConfig by remember { mutableStateOf<ConfigType?>(null) }

    if (selectedConfig == null) {
        // Menú principal de configuración
        ConfigurationMenu(onConfigSelected = { selectedConfig = it })
    } else {
        // Pantalla de gestión para el tipo de configuración seleccionado
        ManagementScreen(
            configType = selectedConfig!!,
            configViewModel = configViewModel,
            onBack = { selectedConfig = null }
        )
    }
}

enum class ConfigType(val title: String) {
    SLA_TYPES("Tipos de SLA"),
    PRIORITIES("Prioridades"),
    ROLES("Roles / Bloques Tecnológicos")
}

@Composable
fun ConfigurationMenu(onConfigSelected: (ConfigType) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Configuración del Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ConfigType.values()) { configType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfigSelected(configType) },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(configType.title, fontSize = 16.sp)
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    configType: ConfigType,
    configViewModel: ConfigurationViewModel,
    onBack: () -> Unit
) {
    val items by when (configType) {
        ConfigType.SLA_TYPES -> configViewModel.slaTypes.collectAsState()
        ConfigType.PRIORITIES -> configViewModel.priorities.collectAsState()
        ConfigType.ROLES -> configViewModel.roles.collectAsState()
    }

    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(configType.title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { itemToEdit = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item, modifier = Modifier.weight(1f))
                    IconButton(onClick = { itemToEdit = item; showDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        when (configType) {
                            ConfigType.SLA_TYPES -> configViewModel.deleteSlaType(item)
                            ConfigType.PRIORITIES -> configViewModel.deletePriority(item)
                            ConfigType.ROLES -> configViewModel.deleteRole(item)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }
    }

    if (showDialog) {
        EditItemDialog(
            item = itemToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { newItem ->
                if (itemToEdit == null) { // Añadir
                    when (configType) {
                        ConfigType.SLA_TYPES -> configViewModel.addSlaType(newItem)
                        ConfigType.PRIORITIES -> configViewModel.addPriority(newItem)
                        ConfigType.ROLES -> configViewModel.addRole(newItem)
                    }
                } else { // Editar
                    when (configType) {
                        ConfigType.SLA_TYPES -> configViewModel.updateSlaType(itemToEdit!!, newItem)
                        ConfigType.PRIORITIES -> configViewModel.updatePriority(itemToEdit!!, newItem)
                        ConfigType.ROLES -> configViewModel.updateRole(itemToEdit!!, newItem)
                    }
                }
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(item: String?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(item ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Añadir nuevo" else "Editar ítem") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nombre") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

package dev.esandamzapp.slatrackerapp.ui.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// --- Colores del Tema (Mismos que Home) ---
val BgBody = Color(0xFFF3F4F6)
val BgCard = Color(0xFFFFFFFF)
val PrimaryDark = Color(0xFF1F2937)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val AccentBlue = Color(0xFF3B82F6)
val StatusDanger = Color(0xFFEF4444)

@Composable
fun ConfigurationScreen(navController: NavController, configViewModel: ConfigurationViewModel = viewModel()) {
    var selectedConfig by remember { mutableStateOf<ConfigType?>(null) }

    // Fondo general
    Box(modifier = Modifier.fillMaxSize().background(BgBody)) {
        if (selectedConfig == null) {
            ConfigurationMenu(onConfigSelected = { selectedConfig = it })
        } else {
            ManagementScreen(
                configType = selectedConfig!!,
                configViewModel = configViewModel,
                onBack = { selectedConfig = null }
            )
        }
    }
}

enum class ConfigType(val title: String, val subtitle: String, val icon: ImageVector) {
    SLA_TYPES("Tipos de SLA", "Define niveles de servicio", Icons.Outlined.Info),
    PRIORITIES("Prioridades", "Gestiona la urgencia", Icons.Outlined.Star), // Warning icon replacement if needed
    ROLES("Bloques Tecnológicos", "Áreas y departamentos", Icons.Outlined.Person)
}

@Composable
fun ConfigurationMenu(onConfigSelected: (ConfigType) -> Unit) {
    Column {
        // 1. Header Curvo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF111827), Color(0xFF374151))
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Administra las variables del sistema",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        // 2. Lista de Opciones (Flotante)
        Column(
            modifier = Modifier
                .offset(y = (-50).dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConfigType.values().forEach { configType ->
                ConfigOptionCard(
                    configType = configType,
                    onClick = { onConfigSelected(configType) }
                )
            }
        }
    }
}

@Composable
fun ConfigOptionCard(configType: ConfigType, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BgBody, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = configType.icon,
                    contentDescription = null,
                    tint = PrimaryDark
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = configType.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = configType.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary
            )
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
        containerColor = BgBody,
        topBar = {
            // TopBar Custom para mantener el estilo
            Surface(
                color = BgCard,
                shadowElevation = 4.dp,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            configType.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = PrimaryDark)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { itemToEdit = null; showDialog = true },
                containerColor = PrimaryDark,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            items(items) { item ->
                ManagementItemCard(
                    text = item,
                    onEdit = { itemToEdit = item; showDialog = true },
                    onDelete = {
                        when (configType) {
                            ConfigType.SLA_TYPES -> configViewModel.deleteSlaType(item)
                            ConfigType.PRIORITIES -> configViewModel.deletePriority(item)
                            ConfigType.ROLES -> configViewModel.deleteRole(item)
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // Espacio para FAB
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

@Composable
fun ManagementItemCard(
    text: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador visual decorativo
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(AccentBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            // Botones de acción
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = StatusDanger)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(item: String?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(item ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = {
            Text(
                if (item == null) "Añadir Nuevo" else "Editar Elemento",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    "Ingresa el nombre del elemento:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedLabelColor = AccentBlue,
                        cursorColor = AccentBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
package dev.esandamzapp.slatrackerapp.ui.configuration

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.esandamzapp.slatrackerapp.data.network.AreaDto
import dev.esandamzapp.slatrackerapp.data.network.PriorityDto

// --- Colores del Tema ---
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
    val context = LocalContext.current
    val errorMessage by configViewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            configViewModel.clearError()
        }
    }

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
    // Menú unificado: Gestiona Tipo + Días en uno solo
    SLA_TYPES("Tipos de Solicitud & SLA", "Definir tipos y sus días límite", Icons.Outlined.Timer),
    PRIORITIES("Prioridades", "Niveles de urgencia", Icons.Outlined.Star),
    ROLES("Áreas / Bloques", "Áreas tecnológicas", Icons.Outlined.Person)
}

@Composable
fun ConfigurationMenu(onConfigSelected: (ConfigType) -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF374151))))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text("Configuración", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text("Administra catálogos y reglas del sistema", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9CA3AF))
            }
        }

        Column(
            modifier = Modifier
                .offset(y = (-50).dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConfigType.values().forEach { configType ->
                ConfigOptionCard(configType = configType, onClick = { onConfigSelected(configType) })
            }
        }
    }
}

@Composable
fun ConfigOptionCard(configType: ConfigType, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(BgBody, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = configType.icon, contentDescription = null, tint = PrimaryDark)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = configType.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Text(text = configType.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
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
    // Observamos los estados (usamos slaUiItems para la gestión unificada)
    val slaItems by configViewModel.slaUiItems.collectAsState()
    val priorities by configViewModel.priorities.collectAsState()
    val roles by configViewModel.roles.collectAsState()
    val isLoading by configViewModel.isLoading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit: Any? by remember { mutableStateOf(null) }

    Scaffold(
        containerColor = BgBody,
        topBar = {
            Surface(color = BgCard, shadowElevation = 4.dp, modifier = Modifier.clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))) {
                CenterAlignedTopAppBar(
                    title = { Text(configType.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver", tint = PrimaryDark) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { itemToEdit = null; showDialog = true },
                containerColor = PrimaryDark, contentColor = Color.White, shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, "Añadir") }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AccentBlue)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    when (configType) {
                        ConfigType.SLA_TYPES -> {
                            // Mostramos los items unificados (Tipo + Días)
                            items(slaItems) { item ->
                                ManagementItemCard(
                                    title = item.nombre, // Descripción del tipo
                                    subtitle = "SLA: ${item.dias} días", // CORREGIDO: Eliminado el código visualmente
                                    onEdit = { itemToEdit = item; showDialog = true },
                                    onDelete = { configViewModel.deleteSlaTypeUnificado(item) }
                                )
                            }
                        }
                        ConfigType.PRIORITIES -> {
                            items(priorities) { item ->
                                ManagementItemCard(
                                    title = item.descripcion,
                                    subtitle = "Nivel: ${item.nivel} | Mult: ${item.slaMultiplier}",
                                    onEdit = { itemToEdit = item; showDialog = true },
                                    onDelete = { item.id?.let { configViewModel.deletePriority(it) } }
                                )
                            }
                        }
                        ConfigType.ROLES -> {
                            items(roles) { item ->
                                ManagementItemCard(
                                    title = item.nombre,
                                    subtitle = item.descripcion ?: "Sin descripción",
                                    onEdit = { itemToEdit = item; showDialog = true },
                                    onDelete = { item.id?.let { configViewModel.deleteRole(it) } }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showDialog) {
        ConfigDialog(
            configType = configType,
            existingItem = itemToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { p1, p2, p3, p4 ->
                if (itemToEdit == null) { // Crear
                    when (configType) {
                        // p1=Nombre(Descripción), p2=Codigo(Secondary), p3=Dias
                        // Generamos un código automático (3 primeras letras) si no se pide al usuario
                        ConfigType.SLA_TYPES -> configViewModel.addSlaTypeUnificado(p1, p1.take(3).uppercase(), p3)
                        ConfigType.PRIORITIES -> configViewModel.addPriority(p1, p3, p4)
                        ConfigType.ROLES -> configViewModel.addRole(p1, p2)
                    }
                } else { // Actualizar
                    when (configType) {
                        ConfigType.SLA_TYPES -> {
                            val item = itemToEdit as SlaTypeUiItem
                            // Mantenemos el código existente o generamos uno nuevo si está vacío
                            val code = if (item.codigo.isNotBlank()) item.codigo else p1.take(3).uppercase()
                            configViewModel.updateSlaTypeUnificado(item, p1, code, p3)
                        }
                        ConfigType.PRIORITIES -> {
                            val item = itemToEdit as PriorityDto
                            item.id?.let { configViewModel.updatePriority(it, p1, p3, p4) }
                        }
                        ConfigType.ROLES -> {
                            val item = itemToEdit as AreaDto
                            item.id?.let { configViewModel.updateRole(it, p1, p2) }
                        }
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ManagementItemCard(title: String, subtitle: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(AccentBlue, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = TextSecondary) }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Eliminar", tint = StatusDanger) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigDialog(
    configType: ConfigType,
    existingItem: Any?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Double) -> Unit
) {
    // Lógica para extraer valores iniciales según el tipo de objeto
    val initialName = when (existingItem) {
        is AreaDto -> existingItem.nombre
        is SlaTypeUiItem -> existingItem.nombre // Nombre del tipo (Descripción)
        is PriorityDto -> existingItem.descripcion
        else -> ""
    }

    val initialSecondary = when (existingItem) {
        is AreaDto -> existingItem.descripcion ?: ""
        // Para SLA_TYPES ignoramos el código en la UI
        else -> ""
    }

    val initialInt = if (existingItem is PriorityDto) existingItem.nivel else if (existingItem is SlaTypeUiItem) existingItem.dias else 0
    val initialDouble = if (existingItem is PriorityDto) existingItem.slaMultiplier else 1.0

    var name by remember { mutableStateOf(initialName) }
    var secondary by remember { mutableStateOf(initialSecondary) }
    var intVal by remember { mutableStateOf(if (initialInt == 0 && existingItem == null) "" else initialInt.toString()) }
    var doubleVal by remember { mutableStateOf(initialDouble.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = { Text(if (existingItem == null) "Nuevo" else "Editar", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Campo 1: Nombre / Descripción (Principal)
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text(if(configType == ConfigType.PRIORITIES) "Descripción" else "Nombre / Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo 2: Descripción secundaria (Solo si NO es prioridad y NO es SLA_TYPES)
                // CORREGIDO: Ocultamos este campo para SLA_TYPES ya que el usuario no gestiona el código
                if (configType != ConfigType.PRIORITIES && configType != ConfigType.SLA_TYPES) {
                    OutlinedTextField(
                        value = secondary, onValueChange = { secondary = it },
                        label = { Text("Descripción Adicional") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo 3: Días (para SLA)
                if (configType == ConfigType.SLA_TYPES) {
                    OutlinedTextField(
                        value = intVal, onValueChange = { intVal = it },
                        label = { Text("Días Umbral (SLA)") }, // Etiqueta clara
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Campo 4: Nivel y Multiplicador (Solo para prioridades)
                if (configType == ConfigType.PRIORITIES) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = intVal, onValueChange = { intVal = it },
                            label = { Text("Nivel") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = doubleVal, onValueChange = { doubleVal = it },
                            label = { Text("Multiplier") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, secondary, intVal.toIntOrNull() ?: 0, doubleVal.toDoubleOrNull() ?: 0.0)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) } },
        shape = RoundedCornerShape(24.dp)
    )
}
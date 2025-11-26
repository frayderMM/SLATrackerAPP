package dev.esandamzapp.slatrackerapp.ui.sla

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.esandamzapp.slatrackerapp.data.remote.SlaRequest
import dev.esandamzapp.slatrackerapp.viewmodel.SlaUiState
import dev.esandamzapp.slatrackerapp.viewmodel.SlaViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

fun formatDateFromDigits(digits: String): String {
    val d = digits.filter { it.isDigit() }.take(8)
    val sb = StringBuilder()
    for (i in d.indices) {
        sb.append(d[i])
        if (i == 1 || i == 3) sb.append("/")
    }
    return sb.toString()
}

fun convertDateToIso(digits: String): String? {
    if (digits.length != 8) return null
    val parser = SimpleDateFormat("ddMMyyyy", Locale.US)
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return try {
        parser.parse(digits)?.let { formatter.format(it) }
    } catch (e: Exception) {
        null
    }
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        val transformed = buildString {
            for (i in digits.indices) {
                append(digits[i])
                if (i == 1 || i == 3) append("/")
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                return offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return (offset - 1)
                return offset - 2
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

@Composable
fun DateField(
    label: String,
    valueDigits: String,
    onDigitsChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = valueDigits,
        onValueChange = { newValue ->
            val digits = newValue.filter { it.isDigit() }.take(8)
            onDigitsChange(digits)
        },
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendario",
                tint = Color(0xFFB8B8B8)
            )
        },
        visualTransformation = DateVisualTransformation(),
        singleLine = true,
        isError = isError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    onClose: () -> Unit = {},
    viewModel: SlaViewModel = viewModel()
) {
    var role by remember { mutableStateOf("") }
    var slaType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var slaList by remember {
        mutableStateOf(
            mutableListOf(
                Triple("SLA1", 30, "Nuevo Personal"),
                Triple("SLA2", 20, "Reemplazo de Personal")
            )
        )
    }

    var showNewSlaDialog by remember { mutableStateOf(false) }

    var requestDateDigits by remember { mutableStateOf("") }
    var entryDateDigits by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    var validationErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }


    HandleUiState(uiState = uiState, onDismiss = {
        viewModel.resetState()
        if (uiState is SlaUiState.Success) {
            onClose()
        }
    })


    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nueva Solicitud de Personal", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Registra una nueva solicitud de contratación para seguimiento del SLA",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Column {
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Nombre del Rol") },
                    placeholder = { Text("Ej: Desarrollador Backend Senior") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Rol",
                            tint = Color(0xFFB8B8B8)
                        )
                    },
                    isError = validationErrors.containsKey("role")
                )
                validationErrors["role"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = slaType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de SLA") },
                        placeholder = { Text("Seleccionar SLA") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        isError = validationErrors.containsKey("sla")
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        slaList.forEach { sla ->
                            DropdownMenuItem(
                                text = { Text("${sla.first} (${sla.second} días)") },
                                onClick = {
                                    slaType = "${sla.first} (${sla.second} días)"
                                    expanded = false
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("➕ Agregar nuevo SLA…", color = Color(0xFFFF7A00)) },
                            onClick = {
                                expanded = false
                                showNewSlaDialog = true
                            }
                        )
                    }
                }
                validationErrors["sla"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                DateField(
                    label = "Fecha de Solicitud",
                    valueDigits = requestDateDigits,
                    onDigitsChange = { requestDateDigits = it },
                    isError = validationErrors.containsKey("requestDate")
                )
                validationErrors["requestDate"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                DateField(
                    label = "Fecha de Ingreso",
                    valueDigits = entryDateDigits,
                    onDigitsChange = { entryDateDigits = it },
                    isError = validationErrors.containsKey("entryDate")
                )
                validationErrors["entryDate"]?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val errors = mutableMapOf<String, String>()
                    if (role.isBlank()) errors["role"] = "El nombre del rol es obligatorio."
                    if (slaType.isBlank()) errors["sla"] = "Debe seleccionar un tipo de SLA."

                    val isoRequestDate = convertDateToIso(requestDateDigits)
                    if (requestDateDigits.length < 8) errors["requestDate"] = "La fecha de solicitud está incompleta."
                    else if (isoRequestDate == null) errors["requestDate"] = "La fecha de solicitud no es válida."

                    val isoEntryDate = convertDateToIso(entryDateDigits)
                     if (entryDateDigits.length < 8) errors["entryDate"] = "La fecha de ingreso está incompleta."
                    else if (isoEntryDate == null) errors["entryDate"] = "La fecha de ingreso no es válida."

                    validationErrors = errors

                    if (errors.isEmpty()) {
                        val slaId = slaType.substringAfter("SLA").substringBefore(" ").toIntOrNull() ?: 0
                        val numDiasSla = slaType.substringAfter("(").substringBefore(" ").toIntOrNull() ?: 0

                        val request = SlaRequest(
                            idPersonal = 2,
                            idRolRegistro = 3,
                            idSla = 3,
                            idArea = 3,
                            idEstadoSolicitud = 1,
                            fechaSolicitud = isoRequestDate!!,
                            fechaIngreso = isoEntryDate!!,
                            numDiasSla = numDiasSla,
                            resumenSla = role,
                            origenDato = "android",
                            creadoPor = 3 // <-- CORREGIDO: ahora coincide con el ID del token.
                        )
                        viewModel.createSla(request)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Solicitud", color = Color.White)
            }

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar", color = Color.Black)
            }
        }
    }

    if (showNewSlaDialog) {

        var name by remember { mutableStateOf("") }
        var days by remember { mutableStateOf("") }
        var categoryExpanded by remember { mutableStateOf(false) }
        var category by remember { mutableStateOf("Nuevo Personal") }

        val categories = listOf("Nuevo Personal", "Reemplazo de Personal", "Otro")

        AlertDialog(
            onDismissRequest = { showNewSlaDialog = false },
            title = { Text("Nuevo SLA") },

            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del SLA") },
                        placeholder = { Text("Ej: SLA3") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Límite de días") },
                        placeholder = { Text("Ej: 35") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Text("Categoría", style = MaterialTheme.typography.labelMedium)

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        category = it
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },

            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank() && days.toIntOrNull() != null) {
                        slaList.add(Triple(name, days.toInt(), category))
                        slaType = "$name (${days.toInt()} días)"
                        showNewSlaDialog = false
                    }
                }) {
                    Text("Crear SLA")
                }
            },

            dismissButton = {
                OutlinedButton(onClick = { showNewSlaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HandleUiState(uiState: SlaUiState, onDismiss: () -> Unit) {
    when (uiState) {
        is SlaUiState.Loading -> {
            Dialog(onDismissRequest = {}) {
                CircularProgressIndicator()
            }
        }
        is SlaUiState.Success -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Éxito") },
                text = { Text("La solicitud se ha creado correctamente.") },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("Aceptar")
                    }
                }
            )
        }
        is SlaUiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Error") },
                text = { Text(uiState.message) },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("Aceptar")
                    }
                }
            )
        }
        is SlaUiState.Idle -> {}
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package dev.esandamzapp.slatrackerapp.ui.sla

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

// ------------------------------------------------------
// (Opcional) Formateador que puede usarse para obtener la
// fecha con barras desde una cadena de dígitos.
// ------------------------------------------------------
fun formatDateFromDigits(digits: String): String {
    val d = digits.filter { it.isDigit() }.take(8)
    val sb = StringBuilder()
    for (i in d.indices) {
        sb.append(d[i])
        if (i == 1 || i == 3) sb.append("/")
    }
    return sb.toString()
}

// ------------------------------------------------------
// VisualTransformation para mostrar dd/mm/aaaa sin
// modificar el texto subyacente (que serán sólo dígitos).
// Esto mantiene el cursor estable.
// ------------------------------------------------------
class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)

        // Construir la cadena transformada con '/'
        val transformed = buildString {
            for (i in digits.indices) {
                append(digits[i])
                if (i == 1 || i == 3) append("/")
            }
        }

        // OffsetMapping: mapea posiciones original <-> transformada
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // offset: posición en original (solo dígitos)
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                return offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                // offset: posición en transformada (incluye '/')
                if (offset <= 2) return offset
                if (offset <= 5) return (offset - 1)
                return offset - 2
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

// ------------------------------------------------------
// CAMPO DE FECHA (usa texto subyacente SIN slashes, pero muestra con slashes)
// value debe ser la cadena de dígitos (ej. "15022025").
// ------------------------------------------------------
@Composable
fun DateField(
    label: String,
    valueDigits: String,
    onDigitsChange: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = valueDigits,
            onValueChange = { newValue ->
                // Aceptar sólo dígitos y limitar a 8 (ddMMyyyy)
                val digits = newValue.filter { it.isDigit() }.take(8)
                onDigitsChange(digits)
            },
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
            singleLine = true
        )
    }
}

// ------------------------------------------------------
// PANTALLA PRINCIPAL
// ------------------------------------------------------
@Composable
fun NewRequestScreen(
    onClose: () -> Unit = {},
    onSubmit: () -> Unit = {}
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

    // Ahora estos estados contienen SÓLO dígitos (ej. "15022025")
    var requestDateDigits by remember { mutableStateOf("") }
    var entryDateDigits by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ------------------------------------------------------
            // HEADER
            // ------------------------------------------------------
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

            // ------------------------------------------------------
            // NOMBRE DEL ROL
            // ------------------------------------------------------
            Column {
                Text("Nombre del Rol", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    placeholder = { Text("Ej: Desarrollador Backend Senior") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Rol",
                            tint = Color(0xFFB8B8B8)
                        )
                    }
                )
            }

            // ------------------------------------------------------
            // DROPDOWN DE SLA
            // ------------------------------------------------------
            Column {
                Text("Tipo de SLA", style = MaterialTheme.typography.labelMedium)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = slaType,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccionar SLA") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
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
            }

            // ------------------------------------------------------
            // FECHA DE SOLICITUD
            // ------------------------------------------------------
            DateField(
                label = "Fecha de Solicitud",
                valueDigits = requestDateDigits,
                onDigitsChange = { requestDateDigits = it }
            )

            // ------------------------------------------------------
            // FECHA DE INGRESO
            // ------------------------------------------------------
            DateField(
                label = "Fecha de Ingreso",
                valueDigits = entryDateDigits,
                onDigitsChange = { entryDateDigits = it }
            )

            // ------------------------------------------------------
            // BOTÓN REGISTRAR
            // ------------------------------------------------------
            Button(
                onClick = {
                    // Aquí puedes convertir a formato dd/mm/yyyy si lo necesitas:
                    val formattedRequest = formatDateFromDigits(requestDateDigits)
                    val formattedEntry = formatDateFromDigits(entryDateDigits)

                    // Haz lo que necesites con las fechas formateadas:
                    // por ejemplo, enviarlas al viewModel o validarlas.
                    onSubmit()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Solicitud", color = Color.White)
            }

            // ------------------------------------------------------
            // BOTÓN CANCELAR
            // ------------------------------------------------------
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

    // ------------------------------------------------------
    // DIÁLOGO PARA NUEVO SLA
    // ------------------------------------------------------
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

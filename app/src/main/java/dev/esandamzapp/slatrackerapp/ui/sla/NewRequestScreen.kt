@file:OptIn(ExperimentalMaterial3Api::class)

package dev.esandamzapp.slatrackerapp.ui.sla

import android.R.attr.category
import android.R.attr.name
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.days

@Composable
fun NewRequestScreen(
    onClose: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var role by remember { mutableStateOf("") }

    var slaType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Lista dinámica de SLA (mockup, sin modelo)
    var slaList by remember {
        mutableStateOf(
            mutableListOf(
                Triple("SLA1", 35, "Nuevo Personal"),
                Triple("SLA2", 20, "Reemplazo de Personal")
            )
        )
    }

    var showNewSlaDialog by remember { mutableStateOf(false) }

    var requestDate by remember { mutableStateOf("") }
    var entryDate by remember { mutableStateOf("") }

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

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nueva Solicitud de Personal", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Registra una solicitud para seguimiento del SLA",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            // ROL
            Column {
                Text("Nombre del Rol", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    placeholder = { Text("Ej: Desarrollador Backend Senior") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // SLA DROPDOWN
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        slaList.forEach { sla ->
                            DropdownMenuItem(
                                text = {
                                    Text("${sla.first} – <${sla.second} días – ${sla.third}")
                                },
                                onClick = {
                                    slaType = "${sla.first} – <${sla.second} días – ${sla.third}"
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

            // FECHA SOLICITUD
            Column {
                Text("Fecha de Solicitud", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = requestDate,
                    onValueChange = { requestDate = it },
                    placeholder = { Text("mm/dd/yyyy") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // FECHA INGRESO
            Column {
                Text("Fecha de Ingreso", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = entryDate,
                    onValueChange = { entryDate = it },
                    placeholder = { Text("mm/dd/yyyy") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // SUBMIT
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Registrar Solicitud", color = Color.White)
            }

            // CANCELAR
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar")
            }
        }
    }

    // DIÁLOGO CREAR SLA (mockup total)
    if (showNewSlaDialog) {

        // ESTADOS MOVIDOS AQUÍ (visibles para todo el diálogo)
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
                        modifier = Modifier.fillMaxWidth()
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
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
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
                        slaType = "$name – <${days.toInt()} días – $category"
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

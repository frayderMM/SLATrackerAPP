package dev.esandamzapp.slatrackerapp.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme

private val PreviewBackground = Color(0xFFF6F2FF)
private val CheckboxPurple = Color(0xFF6C40FF)

@Composable
fun ReportPreviewScreen(
    onClose: () -> Unit,
    onExportPDF: () -> Unit,
    onSendEmail: () -> Unit
) {
    var colRol by remember { mutableStateOf(true) }
    var colSla by remember { mutableStateOf(true) }
    var colDias by remember { mutableStateOf(true) }
    var colEstado by remember { mutableStateOf(true) }
    var colFechaSol by remember { mutableStateOf(false) }
    var colFechaIng by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 520.dp, max = 650.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = PreviewBackground,
        tonalElevation = 6.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ---------- HEADER ----------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Previsualización del Reporte SLA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClose) {
                        Text(
                            text = "Cerrar",
                            color = Color(0xFF5C3BFF),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ---------- FILTROS APLICADOS ----------
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Filtros Aplicados",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text("Periodo: Sin especificar - Sin especificar", fontSize = 13.sp)
                        Text("Tipo SLA: Todos", fontSize = 13.sp)
                        Text("Rol/Área: Todos", fontSize = 13.sp)
                        Text("Total de registros: 8", fontSize = 13.sp)
                    }
                }
            }

            // ---------- SECCIÓN COLUMNAS ----------
            item {
                Text(
                    text = "Seleccionar columnas a incluir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LabeledCheckbox(
                        label = "Rol",
                        checked = colRol,
                        onCheckedChange = { colRol = it }
                    )
                    LabeledCheckbox(
                        label = "Tipo SLA",
                        checked = colSla,
                        onCheckedChange = { colSla = it }
                    )
                    LabeledCheckbox(
                        label = "Días",
                        checked = colDias,
                        onCheckedChange = { colDias = it }
                    )
                    LabeledCheckbox(
                        label = "Cumple/No cumple",
                        checked = colEstado,
                        onCheckedChange = { colEstado = it }
                    )
                    LabeledCheckbox(
                        label = "Fecha Solicitud",
                        checked = colFechaSol,
                        onCheckedChange = { colFechaSol = it }
                    )
                    LabeledCheckbox(
                        label = "Fecha Ingreso",
                        checked = colFechaIng,
                        onCheckedChange = { colFechaIng = it }
                    )
                }
            }

            // ---------- RESUMEN DE DATOS MOCK ----------
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Resumen de Datos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.height(4.dp))

                        PreviewRow(
                            rol = "Desarrollador Backend",
                            sla = "SLA1",
                            dias = "25",
                            estado = "Cumple"
                        )
                        PreviewRow(
                            rol = "Gerente RH",
                            sla = "SLA2",
                            dias = "46",
                            estado = "No cumple"
                        )
                        PreviewRow(
                            rol = "Analista Datos",
                            sla = "SLA1",
                            dias = "23",
                            estado = "Cumple"
                        )
                    }
                }
            }

            // ---------- BOTONES ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onExportPDF,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C3BFF)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Exportar PDF",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onSendEmail,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C3BFF)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Enviar por correo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = CheckboxPurple,
                uncheckedColor = CheckboxPurple,
                checkmarkColor = Color.White
            )
        )
        Text(
            text = label,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun PreviewRow(
    rol: String,
    sla: String,
    dias: String,
    estado: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1.4f)) {
            Text(rol, fontSize = 13.sp)
        }
        Text(sla, fontSize = 13.sp, modifier = Modifier.weight(0.6f))
        Text(dias, fontSize = 13.sp, modifier = Modifier.weight(0.6f))
        Text(estado, fontSize = 13.sp, modifier = Modifier.weight(0.9f))
    }
}

// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportPreviewScreenPreview() {
    SLATrackerAPPTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000).copy(alpha = 0.25f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            ReportPreviewScreen(
                onClose = {},
                onExportPDF = {},
                onSendEmail = {}
            )
        }
    }
}

package dev.esandamzapp.slatrackerapp.ui.loadProcessing

// ----------------------------
// IMPORTS NECESARIOS
// ----------------------------
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image

// ----------------------------
// UI PRINCIPAL
// ----------------------------
@Composable
fun ImportarDatosExcelScreen(
    onDescargarPlantilla: () -> Unit = {},
    onSeleccionarArchivo: () -> Unit = {},
    onCancelar: () -> Unit = {},
    onImportar: () -> Unit = {},
    registrosCount: Int = 0
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // ----------------------------
                // TÍTULO
                // ----------------------------
                Text(
                    "Importar Datos desde Excel",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Sube un archivo Excel con las solicitudes de personal " +
                            "para analizar automáticamente el cumplimiento de SLA.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // ----------------------------
                // TARJETA DE PLANTILLA
                // ----------------------------
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "¿Primera vez? Usa nuestra plantilla",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Descarga el formato oficial con ejemplos y columnas pre-configuradas",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Text(
                            "Columnas requeridas:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("- Nombre del Rol")
                            Text("- Tipo de SLA (SLA1 o SLA2)")
                            Text("- Fecha de Solicitud (dd/mm/yyyy)")
                            Text("- Fecha de Ingreso (dd/mm/yyyy)")
                        }

                        Button(
                            onClick = onDescargarPlantilla,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Descargar Plantilla Excel")
                        }
                    }
                }

                // ----------------------------
                // CUADRO PARA SUBIR ARCHIVO
                // ----------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .border(
                            BorderStroke(2.dp, Color.LightGray),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSeleccionarArchivo() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_upload),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.Gray
                        )

                        Spacer(Modifier.height(10.dp))

                        Text("Haz clic para seleccionar un archivo Excel", color = Color.Gray)
                        Text(
                            "Formatos soportados: .xlsx, .xls",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                // ----------------------------
                // BOTONES INFERIORES
                // ----------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedButton(onClick = onCancelar) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = onImportar,
                        enabled = registrosCount > 0
                    ) {
                        Text("Importar $registrosCount registros")
                    }
                }
            }
        }
    }
}

// ----------------------------
// PREVIEW
// ----------------------------
@Preview(showBackground = true)
@Composable
fun PreviewImportarDatosExcel() {
    MaterialTheme {
        ImportarDatosExcelScreen(registrosCount = 0)
    }
}

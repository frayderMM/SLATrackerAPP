package dev.esandamzapp.slatrackerapp.ui.Prediction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme

@Composable
fun PredictionScreen() {
    var requestDate by remember { mutableStateOf("") }
    var entryDate by remember { mutableStateOf("") }
    var simulationResult by remember { mutableStateOf<String?>(null) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Predicción y Simulación de SLA",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            // US-016: Predicción de cumplimiento SLA
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tendencia de Cumplimiento",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Gráfico de Tendencia de SLA (Mock)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // US-017: Simular escenarios
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Simular Escenario", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ingrese las fechas para estimar el cumplimiento.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = requestDate,
                        onValueChange = { requestDate = it },
                        label = { Text("Fecha de Solicitud (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = entryDate,
                        onValueChange = { entryDate = it },
                        label = { Text("Fecha de Ingreso (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            simulationResult = if (requestDate.isNotBlank() && entryDate.isNotBlank()) "Cumple" else "No Cumple"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simular Cumplimiento")
                    }
                    simulationResult?.let {
                        val resultColor = if (it == "Cumple") Color.Green.copy(alpha = 0.7f) else Color.Red.copy(alpha = 0.7f)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Resultado Estimado: $it",
                            style = MaterialTheme.typography.titleMedium,
                            color = resultColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PredictionScreenPreview() {
    SLATrackerAPPTheme {
        PredictionScreen()
    }
}
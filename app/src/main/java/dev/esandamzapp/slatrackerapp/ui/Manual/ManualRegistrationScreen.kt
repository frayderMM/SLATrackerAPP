package dev.esandamzapp.slatrackerapp.ui.Manual

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme

@Composable
fun ManualRegistrationScreen() {
    var role by remember { mutableStateOf("") }
    var slaType by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var slaDays by remember { mutableStateOf("") } // Asumiendo que esto podría ser calculado
    var registrationMessage by remember { mutableStateOf<String?>(null) }
    val showMessage = registrationMessage != null

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro Manual de Solicitud",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Rol del solicitante") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = slaType,
                        onValueChange = { slaType = it },
                        label = { Text("Tipo de SLA") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Fecha de Inicio (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fecha de Fin (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = slaDays,
                        onValueChange = { }, // Es de solo lectura
                        readOnly = true,
                        label = { Text("Días de SLA (calculado)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (role.isNotBlank() && slaType.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()) {
                        // TODO: Implementar validación de fecha y cálculo de días SLA
                        // TODO: Implementar lógica de almacenamiento local (US-019)
                        registrationMessage = "Solicitud registrada con éxito"
                    } else {
                        registrationMessage = "Por favor, complete todos los campos requeridos."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrar Solicitud")
            }

            if (showMessage) {
                val message = registrationMessage ?: ""
                val isSuccess = message.contains("éxito")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManualRegistrationScreenPreview() {
    SLATrackerAPPTheme {
        ManualRegistrationScreen()
    }
}
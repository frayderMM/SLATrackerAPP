package dev.esandamzapp.slatrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.esandamzapp.slatrackerapp.ui.notifications.NotificacionesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Mostramos directamente la pantalla de notificaciones para probar la conexión
            NotificacionesScreen(onBack = { /* No hay acción de regreso en este contexto */ })
        }
    }
}

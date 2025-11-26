package dev.esandamzapp.slatrackerapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    // --- IMPORTANTE ---
    // Reemplaza esta cadena con un token JWT válido obtenido de tu backend.
    private const val FAKE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIzIiwidW5pcXVlX25hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJuYmYiOjE3NjQxNzcyMzMsImV4cCI6MTc2NDE4MDgzMywiaWF0IjoxNzY0MTc3MjMzLCJpc3MiOiJzaW5ndWxhIiwiYXVkIjoic2luZ3VsYV91c2VycyJ9.r-hkhjp8srO5yS9c7fPKJbQbpZ7JUe7V9ghsP3v42gU"

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        // Instala el plugin DefaultRequest para añadir la cabecera de autorización
        // a todas las peticiones salientes.
        install(DefaultRequest) {
            header("Authorization", "Bearer $FAKE_TOKEN")
        }
    }
}

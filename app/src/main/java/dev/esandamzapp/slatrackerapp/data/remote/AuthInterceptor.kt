package dev.esandamzapp.slatrackerapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    // TODO: Reemplazar este token por uno obtenido dinámicamente tras el login.
    private val hardcodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIzIiwidW5pcXVlX25hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJuYmYiOjE3NjQxNzcyMzMsImV4cCI6MTc2NDE4MDgzMywiaWF0IjoxNzY0MTc3MjMzLCJpc3MiOiJzaW5ndWxhIiwiYXVkIjoic2luZ3VsYV91c2VycyJ9.r-hkhjp8srO5yS9c7fPKJbQbpZ7JUe7V9ghsP3v42gU"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlPath = originalRequest.url.encodedPath
        
        // ¡LÓGICA CLAVE! Si la petición es para el login, no añadas el token.
        // El login no requiere autenticación porque es el proceso que obtiene el token.
        if (urlPath.contains("auth/login") || urlPath.endsWith("auth/login")) {
            return chain.proceed(originalRequest)
        }
        
        // Para todas las demás peticiones, añade el token de autorización
        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer $hardcodedToken")
        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}

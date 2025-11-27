package dev.esandamzapp.slatrackerapp.data.remote

import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginRequest
import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginResponse
import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/api/Usuarios/authenticate")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("/api/Usuarios/{id}/perfil-completo")
    suspend fun getPerfilCompleto(
        @Path("id") idUsuario: Int,
        @Header("Authorization") token: String
    ): Response<PerfilCompletoResponse>
}

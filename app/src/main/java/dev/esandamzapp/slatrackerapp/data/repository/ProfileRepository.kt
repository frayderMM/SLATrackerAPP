package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.UsuarioDto
import retrofit2.HttpException

class ProfileRepository {

    private val api = ApiClient.apiService

    suspend fun getUsuario(userId: Int): UsuarioDto {
        val response = api.getUsuario(userId)

        if (!response.isSuccessful)
            throw HttpException(response)

        return response.body()!!
    }
}

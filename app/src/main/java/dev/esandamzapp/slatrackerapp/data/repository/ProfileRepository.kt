package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.PerfilCompletoResponse
import retrofit2.HttpException

class ProfileRepository {

    private val api = ApiClient.apiService

    suspend fun getPerfilCompleto(token: String, userId: Int): PerfilCompletoResponse {
        return api.getPerfilCompleto(userId)
    }
}

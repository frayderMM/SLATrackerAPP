package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginRequest
import dev.esandamzapp.slatrackerapp.data.remote.dto.LoginResponse

class AuthRepository {

    private val api = ApiClient.apiService

    suspend fun login(username: String, password: String): LoginResponse {
        return api.login(LoginRequest(username, password))
    }
}

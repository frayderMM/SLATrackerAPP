package dev.esandamzapp.slatrackerapp.data.repository

import dev.esandamzapp.slatrackerapp.data.remote.ApiClient
import dev.esandamzapp.slatrackerapp.data.remote.dto.ConfigSla
import dev.esandamzapp.slatrackerapp.data.remote.dto.DashboardSlaDto
import dev.esandamzapp.slatrackerapp.data.remote.dto.DashboardStatsDto
import dev.esandamzapp.slatrackerapp.data.remote.dto.RolRegistroDto

class StatisticsRepository {

    private val api = ApiClient.apiService

    suspend fun getDashboardData(
        slaCode: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        bloqueTech: String? = null
    ): Result<List<DashboardSlaDto>> {
        return try {
            val response = api.getDashboardData(slaCode, startDate, endDate, bloqueTech)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error al obtener datos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardStatistics(): Result<DashboardStatsDto> {
        return try {
            val response = api.getDashboardStatistics()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error al obtener estadísticas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConfigSla(): Result<List<ConfigSla>> {
        return try {
            val response = api.getConfigSla()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener configuración SLA: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRolesRegistro(): Result<List<RolRegistroDto>> {
        return try {
            val response = api.getRolesRegistro()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener roles de registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

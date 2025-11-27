package dev.esandamzapp.slatrackerapp.data.network

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL BASE: Puerto 5000 (Docker / VS Code)
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // 1. Configuración de GSON para ser más tolerante a fallos de formato
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // 2. Cliente HTTP personalizado con LOGS COMPLETOS
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d("API_DEBUG", "--> Enviando: ${request.method()} ${request.url()}")

            try {
                val response = chain.proceed(request)

                // TRUCO: Leemos el cuerpo de la respuesta sin consumirlo para poder imprimirlo
                val responseBody = response.peekBody(Long.MAX_VALUE)
                val jsonString = responseBody.string()

                Log.d("API_DEBUG", "<-- ${response.code()} ${request.url()}")
                // AQUÍ VERÁS TUS DATOS REALES EN EL LOGCAT:
                Log.d("API_DEBUG", "JSON RECIBIDO: $jsonString")

                response
            } catch (e: Exception) {
                Log.e("API_DEBUG", "<-- FALLO RED: ${e.message}")
                throw e
            }
        }
        .build()

    // 3. Instancia de Retrofit
    val api: SlaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Usamos nuestro cliente configurado
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SlaApiService::class.java)
    }
}
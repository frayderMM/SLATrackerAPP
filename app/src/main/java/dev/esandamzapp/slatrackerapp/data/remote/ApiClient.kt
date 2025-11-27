package dev.esandamzapp.slatrackerapp.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Asegúrate de que esta URL sea la correcta para tu backend.
    // Para emulador Android: http://10.0.2.2:PUERTO
    // Para dispositivo físico: http://TU_IP_LOCAL:PUERTO (ej: http://192.168.100.9:5192)
    // IMPORTANTE: Si cambias entre emulador y dispositivo físico, actualiza esta IP
    
    // SOLUCIÓN TEMPORAL: Usa el emulador hasta que el backend esté configurado correctamente
    private const val BASE_URL = "http://10.0.2.2:5192/"  // Para EMULADOR (funciona con backend en localhost)
    // private const val BASE_URL = "http://192.168.100.9:5192/"  // Para DISPOSITIVO FÍSICO (requiere que backend escuche en 0.0.0.0:5192)

    // Interceptor para ver los logs de las peticiones en Logcat (muy útil para depurar).
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp que incluye el interceptor de logs y el de autenticación.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor()) // Añadimos el interceptor de autenticación
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión: 30 segundos
        .readTimeout(30, TimeUnit.SECONDS) // Timeout de lectura: 30 segundos
        .writeTimeout(30, TimeUnit.SECONDS) // Timeout de escritura: 30 segundos
        .build()
    
    init {
        Log.d("ApiClient", "Base URL configurada: $BASE_URL")
    }

    /**
     * Instancia de Retrofit configurada. 
     * Es un `lazy` delegate, lo que significa que se creará solo una vez, la primera vez que se acceda a él.
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Usamos Gson para la conversión de JSON
            .build()
            .create(ApiService::class.java)
    }
}

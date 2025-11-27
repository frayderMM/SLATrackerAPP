package dev.esandamzapp.slatrackerapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Asegúrate de que esta URL sea la correcta para tu backend.
    private const val BASE_URL = "http://10.0.2.2:5192/"

    // Interceptor para ver los logs de las peticiones en Logcat (muy útil para depurar).
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp que incluye el interceptor de logs y el de autenticación.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor()) // Añadimos el interceptor de autenticación
        .build()

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

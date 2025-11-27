package dev.esandamzapp.slatrackerapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL BASE:
    // 10.0.2.2 -> Es la IP especial para que el emulador de Android acceda al localhost de tu PC.
    // 5192 -> Es el puerto HTTP donde está corriendo tu backend .NET.
    private const val BASE_URL = "http://10.0.2.2:5192/"

    val api: SlaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SlaApiService::class.java)
    }
}
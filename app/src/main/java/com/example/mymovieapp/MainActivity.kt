package com.example.mymovieapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface { }
            }
        }

        // ==== ЗАПРОС К API TMDb ====
        val apiKey = BuildConfig.TMDB_API_KEY

        // Отключаем прокси и принудительно используем TLS 1.2
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .build()

        val client = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .connectionSpecs(listOf(spec))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TmdbApiService::class.java)

        val call = service.getTopRatedMovies(apiKey)
        call.enqueue(object : Callback<TopRatedResponse> {
            override fun onResponse(
                call: Call<TopRatedResponse>,
                response: Response<TopRatedResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    Log.d("TMDb_Test", "Успех! Получено фильмов: ${movies?.size}")
                    movies?.forEach { movie ->
                        Log.d("TMDb_Test", "Фильм: ${movie.title}, Рейтинг: ${movie.voteAverage}")
                    }
                } else {
                    Log.e("TMDb_Test", "Ошибка сервера: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TopRatedResponse>, t: Throwable) {
                Log.e("TMDb_Test", "Ошибка сети: ${t.message}")
            }
        })
    }
}
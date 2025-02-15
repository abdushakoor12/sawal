package com.abdushakoor12.sawal.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    fun getInstance(): Retrofit {
        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val originalRequest = chain.request()
//                chain.proceed(chain.request())
//            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
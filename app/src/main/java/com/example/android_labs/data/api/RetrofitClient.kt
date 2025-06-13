package com.example.android_labs.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val RickAndMorty: RickAndMortyApi by lazy {
            retrofit.create(RickAndMortyApi::class.java)
        }
    }
}
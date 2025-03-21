package com.example.android_labs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


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

interface RickAndMortyApi {
    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character
}
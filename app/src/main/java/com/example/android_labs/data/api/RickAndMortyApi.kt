package com.example.android_labs.data.api

import com.example.android_labs.data.models.Character
import retrofit2.http.GET
import retrofit2.http.Path

interface RickAndMortyApi {
    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character
}
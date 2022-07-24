package com.ssong_develop.retrofitcallbackconvertproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface apiService {

    @GET("character")
    fun fetchCharacter(
        @Query("page") page: Int
    ): Call<Wrapper<Info, Characters>>
}
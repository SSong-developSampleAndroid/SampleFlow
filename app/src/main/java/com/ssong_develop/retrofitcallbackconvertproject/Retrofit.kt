package com.ssong_develop.retrofitcallbackconvertproject

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FakeRetrofit {

    private val json by lazy {
        Json {
            coerceInputValues = true
        }
    }

    private var INSTANCE : Retrofit? = null

    fun provideRetrofit() : Retrofit {
        if (INSTANCE == null) {
            INSTANCE = Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return INSTANCE!!
    }
}
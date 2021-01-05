package com.example.retrofitdemo

import android.app.Application
import cbfg.http.HttpManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RelApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.apiopen.top")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        HttpManager.init(okHttpClient, retrofit)
    }
}
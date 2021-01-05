package com.example.retrofitdemo

import android.app.Application
import cbfg.http.HttpManager
import retrofit2.converter.moshi.MoshiConverterFactory

class RelApp : Application() {
    override fun onCreate() {
        super.onCreate()
        HttpManager.init { _, retrofitBuilder ->
            retrofitBuilder.baseUrl("https://api.apiopen.top")
                .addConverterFactory(MoshiConverterFactory.create())
        }
    }
}
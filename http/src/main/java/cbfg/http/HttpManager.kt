package cbfg.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object HttpManager {
    lateinit var okHttpClient: OkHttpClient
        private set

    lateinit var retrofit: Retrofit
        private set

    fun init(okHttpClient: OkHttpClient, retrofit: Retrofit) {
        this.okHttpClient = okHttpClient
        this.retrofit = retrofit
    }
}
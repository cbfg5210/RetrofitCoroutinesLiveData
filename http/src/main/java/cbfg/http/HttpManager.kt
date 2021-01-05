package cbfg.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object HttpManager {
    lateinit var okHttpClient: OkHttpClient
        private set

    lateinit var retrofit: Retrofit
        private set

    fun init(action: (okHttpClientBuilder: OkHttpClient.Builder, retrofitBuilder: Retrofit.Builder) -> Unit) {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val retrofitBuilder = Retrofit.Builder()
        action(okHttpClientBuilder, retrofitBuilder)
        okHttpClient = okHttpClientBuilder.build()
        retrofit = retrofitBuilder.client(okHttpClient).build()
    }
}
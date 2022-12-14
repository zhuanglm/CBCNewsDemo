package com.cbc.newsdemo.data.api

import com.cbc.newsdemo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private fun getRetrofit(baseURL: String): Retrofit {
        //val apiInterceptor = ApiClientRequestInterceptor()
        val clientBuilder = OkHttpClient.Builder()
                /*.addInterceptor(ApiClientRequestInterceptor())*/
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
                .baseUrl(baseURL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build() //Doesn't require the adapter
    }

    //val apiService: ApiService = getRetrofit().create(ApiService::class.java)

    fun apiService(url: String): NewsApiService{
        return getRetrofit(url).create(NewsApiService::class.java)
    }
}

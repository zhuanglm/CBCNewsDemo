package com.cbc.newsdemo.data.api

import com.cbc.newsdemo.data.models.Article
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v1/items")

    //function
    //async
    //coroutine
    suspend fun getNews(
    @Query("lineupSlug")
    lineupSlug: String= "news",

    ):Response<MutableList<Article>>

}
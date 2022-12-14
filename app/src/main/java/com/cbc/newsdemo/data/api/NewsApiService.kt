package com.cbc.newsdemo.data.api

import com.cbc.newsdemo.data.models.NewsApiResponse
import com.cbc.newsdemo.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    /* here we define single request that we can execute from the code
     */
    //we use Api interface to access api for request
    //function to get all breaking news from the api
    // we need to specify the type of http request -GET here
    //and we return the responses from the API

    @GET("v2/top-headlines")

    //function
    //async
    //coroutine
    suspend fun getBreakingNews(
        //request parameters to function
    @Query("country")
    countryCode: String = "us", //default to us

    @Query("page")  //to paginate the request
    pageNumber: Int= 1,

    @Query("apiKey")
    apiKey: String= Constants.API_KEY

    ):Response<NewsApiResponse> //return response


    @GET("v2/everything")

    //function
    //async
    //coroutine
    suspend fun searchForNews(
        //request parameters to function
        @Query("q")
        searchQuery: String,
        @Query("page")  //to paginate the request
        pageNumber: Int= 1,
        @Query("apiKey")
        apiKey: String= Constants.API_KEY
    ):Response<NewsApiResponse> //return response
}
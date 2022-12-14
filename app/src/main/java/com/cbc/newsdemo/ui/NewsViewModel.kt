package com.cbc.newsdemo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cbc.newsdemo.DemoApplication
import com.cbc.newsdemo.data.models.NewsApiResponse
import com.cbc.newsdemo.data.repository.NewsRepository
import com.cbc.newsdemo.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val news: MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    var newsPagination= 1
    var breakingNewsResponse : NewsApiResponse? = null

    init {
        getAllNews("ca")
    }

    private fun handleBreakingNewsResponse(response: Response<NewsApiResponse>): Resource<NewsApiResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                newsPagination++
                if (breakingNewsResponse== null){
                    breakingNewsResponse= resultResponse //if first page save the result to the response
                }else{
                    val oldArticles= breakingNewsResponse?.articles //else, add all articles to old
                    val newArticle= resultResponse.articles //add new response to new
                    oldArticles?.addAll(newArticle) //add new articles to old articles
                }
                return  Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getAllNews(countryCode: String)= viewModelScope.launch {
        fetchNews(countryCode)
    }

    private suspend fun fetchNews(countryCode: String){
        news.postValue(Resource.Loading())
        try{
            if (isInternetConnected()){
                val response= newsRepository.getBreakingNews(countryCode, newsPagination)
                //handling response
                news.postValue(handleBreakingNewsResponse(response))
            }else{
                news.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> news.postValue(Resource.Error("Network Failure"))
                else-> news.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun isInternetConnected(): Boolean{
        val connectivityManager= getApplication<DemoApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork= connectivityManager.activeNetwork?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
}
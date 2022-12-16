package com.cbc.newsdemo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cbc.newsdemo.DemoApplication
import com.cbc.newsdemo.data.models.Article
import com.cbc.newsdemo.data.repository.NewsRepository
import com.cbc.newsdemo.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val news: MutableLiveData<Resource<MutableList<Article>>> = MutableLiveData()
    var newsResponse : MutableList<Article>? = null
    private var typeSet: MutableSet<String> = hashSetOf()

    init {
        getAllNews()
    }

    private fun handleNewsResponse(response: Response<MutableList<Article>>): Resource<MutableList<Article>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                newsResponse = resultResponse
                for (i in resultResponse) {
                    i.type?.let {
                        typeSet?.add(it)
                    }
                }
                return Resource.Success(newsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getAllNews()= viewModelScope.launch {
        fetchNews()
    }

    private suspend fun fetchNews(){
        news.postValue(Resource.Loading())
        try{
            if (isInternetConnected()){
                val response= newsRepository.getNews("news")
                //handling response
                news.postValue(handleNewsResponse(response))
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

    fun getSavedArticle()= newsRepository.getSavedNews()

    fun getSavedArticleBy(id: Int)= newsRepository.getSavedNewsBy(id)

    fun saveArticle(article: Article)= viewModelScope.launch {
        newsRepository.insert(article)
    }

    fun deleteArticle(article: Article)= viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
}
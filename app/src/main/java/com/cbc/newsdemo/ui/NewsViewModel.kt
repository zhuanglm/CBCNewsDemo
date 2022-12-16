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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, private val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val news: MutableLiveData<Resource<MutableList<Article>>> = MutableLiveData()
    val connection: MutableLiveData<Boolean> = MutableLiveData()
    private var newsResponse : MutableList<Article>? = null
    private val allResponse: MutableList<Article> = mutableListOf()
    var typeSet: MutableSet<String> = hashSetOf()
    var availableTypes: MutableSet<String> = hashSetOf()

    init {
        getAllNews()
    }

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    fun onFilterDialogClicked() {
        _showDialog.value = true
        availableTypes.addAll(typeSet)
    }

    fun onDialogConfirm() {
        _showDialog.value = false
        val tempResponse : MutableList<Article> = mutableListOf()
        tempResponse.addAll(allResponse)
        news.postValue(handleNewsResponse(Response.success(tempResponse)))
    }

    fun onDialogDismiss() {
        _showDialog.value = false
    }

    private fun filterResultByType() {
        if(availableTypes.size > 0) {
            newsResponse?.clear()
            allResponse.forEach { article ->
                availableTypes.forEach { type ->
                    if(type == article.type) {
                        if(newsResponse == null)
                            newsResponse = mutableListOf(article)
                        else
                            newsResponse?.add(article)
                    }
                }
            }
        }

    }

    private fun handleNewsResponse(response: Response<MutableList<Article>>): Resource<MutableList<Article>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                newsResponse = resultResponse
                for (i in resultResponse) {
                    i.type?.let {
                        typeSet.add(it)
                    }
                }

                if(availableTypes.size == 0 || availableTypes.size == typeSet.size) {
                    allResponse.clear()
                    allResponse.addAll(resultResponse)
                }
                else
                    filterResultByType()

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
                val response= newsRepository.getNews()
                //handling response
                news.postValue(handleNewsResponse(response))
                connection.postValue(true)
            }else{
                news.postValue(Resource.Error("No Internet Connection"))
                connection.postValue(false)
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
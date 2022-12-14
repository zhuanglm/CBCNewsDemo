package com.cbc.newsdemo.data.repository

import com.cbc.newsdemo.data.api.RetrofitBuilder
import com.cbc.newsdemo.data.db.ArticleDatabase
import com.cbc.newsdemo.data.models.Article
import com.cbc.newsdemo.utils.Constants


class NewsRepository(
    val db: ArticleDatabase //parameter
) {

    suspend fun getNews(lineupSlug:String)=
        RetrofitBuilder.apiService(Constants.BASE_URL).getNews("news")


    /*
    function to insert article to db
     */
    suspend fun upsert(article: Article)=
        db.getArticleDao().upsert(article)

    /*
    function to get saved news from db
     */
    fun getSavedNews()=
        db.getArticleDao().getAllArticles()

    /*
    function to delete articles from db
     */
    suspend fun deleteArticle(article: Article)=
        db.getArticleDao().deleteArticle(article)
}
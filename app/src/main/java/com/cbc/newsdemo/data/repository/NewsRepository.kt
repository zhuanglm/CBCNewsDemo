package com.cbc.newsdemo.data.repository

import com.cbc.newsdemo.data.api.RetrofitBuilder
import com.cbc.newsdemo.data.db.ArticleDatabase
import com.cbc.newsdemo.data.models.Article
import com.cbc.newsdemo.utils.Constants


class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getNews(lineupSlug:String)=
        RetrofitBuilder.apiService(Constants.BASE_URL).getNews("news")

    suspend fun insert(article: Article)=
        db.getArticleDao().insert(article)

    fun getSavedNews()=
        db.getArticleDao().getAllArticles()

    fun getSavedNewsBy(id: Int)=
        db.getArticleDao().getArticle(id)

    suspend fun deleteArticle(article: Article)=
        db.getArticleDao().deleteArticle(article)
}
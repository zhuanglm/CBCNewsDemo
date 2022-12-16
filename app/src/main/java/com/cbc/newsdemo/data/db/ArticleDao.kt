package com.cbc.newsdemo.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cbc.newsdemo.data.models.Article

/*
Data Access Object
 */
@Dao //annotate to let room know that this is the interface that defines the function

interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long // function(parameter):return --> here we return ID

    @Query("SELECT* FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Query("SELECT* FROM articles WHERE id == :id")
    fun getArticle(id: Int):LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}
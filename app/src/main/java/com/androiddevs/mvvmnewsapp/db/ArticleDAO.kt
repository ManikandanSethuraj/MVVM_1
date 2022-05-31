package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDAO {

    /* upsert meaning update or insert */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long

    @Query("SELECT * FROM articles")
    fun getAllArticles() : LiveData<List<Article>>


    @Query("SELECT COUNT(*) FROM articles")
    fun getTotalCount() : Int

    @Delete
    suspend fun deleteArticle(article: Article)


}
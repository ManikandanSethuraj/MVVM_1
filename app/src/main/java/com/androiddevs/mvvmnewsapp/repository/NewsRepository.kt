package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

/**
 * Repository is the place where you bring the data from the database and the remote data
 */

class NewsRepository(
    val articleDatabase: ArticleDatabase
)   {

    suspend fun getBreakingNews(countryCode : String, pageNum : Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNum)

    suspend fun getSearchNews(query : String, pageNum: Int) =
        RetrofitInstance.api.searchForNews(query, pageNum)

    suspend fun upsertArticle(article: Article) = articleDatabase.getArticleDao().upsert(article)

    fun getAllSavedArticles() = articleDatabase.getArticleDao().getAllArticles()

    fun totalCount() = articleDatabase.getArticleDao().getTotalCount()

    suspend fun deleteArticle(article: Article) = articleDatabase.getArticleDao().deleteArticle(article)


}
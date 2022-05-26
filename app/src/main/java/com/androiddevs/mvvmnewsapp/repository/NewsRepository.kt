package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase

/**
 * Repository is the place where you bring the data from the database and the remote data
 */

class NewsRepository(
    val articleDatabase: ArticleDatabase
)   {

    suspend fun getBreakingNews(countryCode : String, pageNum : Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNum)

}
package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/* This @GET functions are network call functions which gonna be asynchronous, so they
* have to run by Coroutines , hence suspend functions */

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode : String = "us",
        @Query("page")
        page : Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery : String,
        @Query("page")
        page : Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ) : Response<NewsResponse>



}
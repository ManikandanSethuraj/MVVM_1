package com.androiddevs.mvvmnewsapp.util

import com.androiddevs.mvvmnewsapp.db.ArticleDatabase


/* Google recommended class for network responses */
/*  */
/* The resources of responses have data and message */
/* Success has data, Error has message and data, Loading is the loading state of Resource */
sealed class Resource<T>(
    val data : T? = null,
    val message : String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T?=null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
package com.androiddevs.mvvmnewsapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.net.TransportInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response


/**
 * @author
 */

class NewsViewModel(
    app: NewsApplication,
val newsRepository: NewsRepository
) : AndroidViewModel(app)  {


    /**
     * The LiveData is used here, so that the fragments can subscribe to these LiveData through Observers.
     * Whenever there is a change in this LiveData (postValue function below), the fragments get notified about it through observers
     */
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()


    // The Page number is implemented here because if it is implemented in the Fragment the value can be changed on Screen rotation.
    var breakingNewsPage = 1
    var breakingNewsResponse : NewsResponse ?= null
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse ?= null

    init {
        getBreakingNews("us")
    }


    /**
     * Since the getBreakingNews of NewsRepository is Suspend Function the getBreakingNews here has to be either suspend Function or have to be in Coroutine Scope.
     *
     */

    fun getBreakingNews(countryCode : String) = viewModelScope.launch {

        safeGetBreakingNews(countryCode)

    //        breakingNews.postValue(Resource.Loading())
//
//        // The NewsRepository's getBreakingNews returns Retrofit response.
//        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
//
//        // Post the Response value
//        breakingNews.postValue(handleBreakingNewsResponse(response))
    }


   private suspend fun safeGetBreakingNews(countryCode: String){
       breakingNews.postValue(Resource.Loading())
       try {
           if (checkInternetConnection()){
               // The NewsRepository's getBreakingNews returns Retrofit response.
               val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)

               // Post the Response value
               breakingNews.postValue(handleBreakingNewsResponse(response))
           }else{
               breakingNews.postValue(Resource.Error("No Internet Connection"))
           }
       } catch (t : Throwable){
           when(t){
               is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
               else -> breakingNews.postValue(Resource.Error("Conversation Error"))
           }
       }

   }

   fun getSearchNews(query: String) = viewModelScope.launch {
       safeGetSearchNews(query)
   //           searchNews.postValue(Resource.Loading())
//           val response = newsRepository.getSearchNews(query, searchNewsPage)
//       searchNews.postValue(handleSearchNews(response))

   }

    private suspend fun safeGetSearchNews(query: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (checkInternetConnection()){
                val response = newsRepository.getSearchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNews(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        }catch (t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversation Error"))
            }
        }
    }

    // The response from the NewsRepository data is extracted : checking if the response is successful or not
    /**
     * @param response is of Retrofit Response
     * @return NewsResponse
     */
  private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>  {
        if (response.isSuccessful){
            response.body()?.let { newsResponse ->

                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = newsResponse
                }else{
                    var oldResponse = breakingNewsResponse?.articles
                    var newResponse = newsResponse?.articles
                    oldResponse?.addAll(newResponse)
                }
                return Resource.Success(breakingNewsResponse ?: newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

   private fun handleSearchNews(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful){
          response.body()?.let {
              searchNewsPage++
              if (searchNewsResponse == null){
                  searchNewsResponse = it
              }else{
                  var oldResponse = searchNewsResponse?.articles
                  var newResponse = it?.articles
                  oldResponse?.addAll(newResponse)
              }
              return Resource.Success(searchNewsResponse ?: it)
          }
        }
        return Resource.Error(response.message())
    }



    fun upsertArticle(article: Article) =
        viewModelScope.launch {
           val value = newsRepository.upsertArticle(article)
            val total = newsRepository.totalCount()
            Log.d("NewViewModel::", total.toString())
        }


    fun totalSavedCount() = newsRepository.totalCount()

    fun getArticles()  = newsRepository.getAllSavedArticles()

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }




    private fun checkInternetConnection() : Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

                return when{
                    capabilities.hasTransport(TRANSPORT_WIFI) ->  true
                    capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                    else -> false
                }

        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
        return false


    }

}
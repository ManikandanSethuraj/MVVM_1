package com.androiddevs.mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * @author
 */

class NewsViewModel(
val newsRepository: NewsRepository
) : ViewModel()  {


    /**
     * The LiveData is used here, so that the fragments can subscribe to these LiveData through Observers.
     * Whenever there is a change in this LiveData (postValue function below), the fragments get notified about it through observers
     */
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()


    // The Page number is implemented here because if it is implemented in the Fragment the value can be changed on Screen rotation.
    var breakingNewsPage = 1
    var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }


    /**
     * Since the getBreakingNews of NewsRepository is Suspend Function the getBreakingNews here has to be either suspend Function or have to be in Coroutine Scope.
     *
     */

    fun getBreakingNews(countryCode : String) = viewModelScope.launch {


        breakingNews.postValue(Resource.Loading())



        // The NewsRepository's getBreakingNews returns Retrofit response.
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)


        // Post the Response value
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }


   fun getSearchNews(query: String) = viewModelScope.launch {
           searchNews.postValue(Resource.Loading())
           val response = newsRepository.getSearchNews(query, searchNewsPage)
       searchNews.postValue(handleSearchNews(response))

   }

    // The response from the NewsRepository data is extracted : checking if the response is successful or not
    /**
     * @param response is of Retrofit Response
     * @return NewsResponse
     */
  private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>  {
        if (response.isSuccessful){
            response.body()?.let { newsResponse ->
                return Resource.Success(newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

   private fun handleSearchNews(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful){
          response.body()?.let {
              return Resource.Success(it)
          }
        }
        return Resource.Error(response.message())
    }


}
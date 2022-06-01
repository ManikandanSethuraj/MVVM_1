package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.*
import retrofit2.Response

class SearchNewsFragment : BasicFragment(R.layout.fragment_search_news){


    private val TAG = "SearchNewsFragment"
    private lateinit var newsAdapter : NewsAdapter
    private lateinit var newsViewModel : NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter()
        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()
        var job : Job ?= null
        etSearch.addTextChangedListener { editableTxt ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editableTxt?.let {
                    if (editableTxt.toString().isNotEmpty()){
                        newsViewModel.getSearchNews(editableTxt.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {
             when(it){
                 is Resource.Success -> {
                     hideProgressBar()
                     it.data?.let {
                         newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults/ Constants.QUERY_PAGE_SIZE +2
                         isLastPage = newsViewModel.searchNewsPage == totalPages
                     }

                 }

                 is Resource.Error -> {
                     hideProgressBar()
                     it.message?.let {
                         Log.d(TAG, "The Error Message is $it")
                         Toast.makeText(context, "Error happened $it", Toast.LENGTH_LONG ).show()
                     }
                 }

                 is Resource.Loading -> {
                     showProgressBar()
                 }
             }
         })


        newsAdapter.setOnItemClickListenser {

        }

    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var scrollListener = object : RecyclerView.OnScrollListener(){


        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // There is no method to know whether the RecyclerView is reached bottom or not so
            // we need to make some calculations with RecyclerView layout manager to find it

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                newsViewModel.getSearchNews(etSearch.text.toString())
                isScrolling = false
            } else {
                rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun hideProgressBar(){
        paginationProgressBar?.let {
            it.visibility = View.INVISIBLE
        }
        isLoading = false
    }

    private fun showProgressBar(){
        paginationProgressBar?.let {
            it.visibility = View.VISIBLE
        }
        isLoading = true
    }

    private fun setUpRecyclerView(){
        rvSearchNews?.apply {
             adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}
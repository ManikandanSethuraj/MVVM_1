package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
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
                         newsAdapter.differ.submitList(it.articles) }

                 }

                 is Resource.Error -> {
                     hideProgressBar()
                     it.message?.let {
                         Log.d(TAG, "The Error Message is $it") }
                 }

                 is Resource.Loading -> {
                     showProgressBar()
                 }
             }
         })

    }

    private fun hideProgressBar(){
        paginationProgressBar?.let {
            it.visibility = View.INVISIBLE
        }
    }

    private fun showProgressBar(){
        paginationProgressBar?.let {
            it.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView(){
        rvSearchNews?.apply {
             adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
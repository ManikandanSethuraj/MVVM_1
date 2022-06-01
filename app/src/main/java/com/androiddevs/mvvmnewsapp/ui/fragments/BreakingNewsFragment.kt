package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import retrofit2.Response

class BreakingNewsFragment : BasicFragment(R.layout.fragment_breaking_news) {

    private val TAG = "BreakingNewsFragment"

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        newsViewModel = (activity as NewsActivity).newsViewModel
        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
         when(response){
             is Resource.Success -> {
                 // Just Running it late Handler is used, not required.
                 Handler().postDelayed(Runnable {
                     hideProgressBar()
                     response.data?.let {
                         newsAdapter.differ.submitList(response.data.articles.toList())
                         val totalPages = response.data.totalResults/ QUERY_PAGE_SIZE + 2
                         Log.d("TotalPages",totalPages.toString())
                         isLastPage = newsViewModel.breakingNewsPage == totalPages
                     } }, 0)
             }
             is Resource.Error -> {
                 hideProgressBar()
                 response.message?.let {
                     Log.d(TAG,"Error happened $it")
                     Toast.makeText(context, "Error happened $it",Toast.LENGTH_LONG ).show()
                 }
             }

             is Resource.Loading ->
             {
                 showProgressBar()
             }
         }
     })

        newsAdapter.setOnItemClickListenser {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                newsViewModel.getBreakingNews("us")
                isScrolling = false
            } else {
                rvBreakingNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun hideProgressBar(){
        paginationProgressBar?.let {
            paginationProgressBar.visibility = View.INVISIBLE
        }
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar?.let {
            paginationProgressBar.visibility = View.VISIBLE
        }
        isLoading = true
    }

  private  fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}
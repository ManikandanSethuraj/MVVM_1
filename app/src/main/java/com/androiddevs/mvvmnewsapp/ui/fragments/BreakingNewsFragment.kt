package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import retrofit2.Response

class BreakingNewsFragment : BasicFragment(R.layout.fragment_breaking_news) {

    private val TAG = "BreakingNewsFragment"

   // lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()



     newsviewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
         when(response){
             is Resource.Success -> {


                 // Just Running it late to

                 Handler().postDelayed(Runnable {
                     hideProgressBar()
                     response.data?.let {
                         newsAdapter.differ.submitList(response.data.articles)
                     } }, 5000)


             }
             is Resource.Error -> {
                 hideProgressBar()
                 response.message?.let {
                     Log.d(TAG,"Error happened $it")
                 }
             }

             is Resource.Loading ->
             {
                 showProgressBar()
             }
         }
     })
    }



    private fun hideProgressBar(){
        paginationProgressBar?.let {
            paginationProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun showProgressBar() {
        paginationProgressBar?.let {
            paginationProgressBar.visibility = View.VISIBLE
        }

    }

    fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}
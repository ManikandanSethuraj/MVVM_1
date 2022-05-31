package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : BasicFragment(R.layout.fragment_saved_news){


    lateinit var newsAdapter : NewsAdapter

    lateinit var newsViewModel : NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsAdapter = NewsAdapter()
        setUpRecyclerView()

        newsViewModel = (activity as NewsActivity).newsViewModel
       // newsAdapter.differ.submitList(newsViewModel.getArticles())


        val totalSaved = newsViewModel.totalSavedCount()
        Log.d("TotalCount::", totalSaved.toString())
        
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view, "Successfully Deleted",Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        newsViewModel.upsertArticle(article)
                    }
                    show()
                }
            }

        }


        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }


        newsAdapter.setOnItemClickListenser {

            val bundle = Bundle().apply {
                putSerializable("article",it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        newsViewModel.getArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

    }

    private fun setUpRecyclerView(){
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager =  LinearLayoutManager(activity)
        }
    }

}
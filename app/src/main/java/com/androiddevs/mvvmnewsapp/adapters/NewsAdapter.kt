package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

/* NotifyDataSetChanged() will always update the whole list so adding the List<Article> like NewsAdapter(List<Article>) will force the adapter
* to update every time the notify is called so we are using DiffUtil class to compare the data
* Also the most important part is the DiffUtil will run in the background without affecting the Main thread */

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    // This call back is used to compare the old List and new List
    private val differCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
           return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // Finds the difference in the lists , this runs in the background as it is Async.
    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            tvTitle.text = article.title
            tvSource.text = article.source.name
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            setOnClickListener { onClickListenser?.let { it(article) } }
        }
    }


    /* Unit is kind of void in Java */
    private var onClickListenser : ((Article) -> Unit)? = null


    private fun setOnItemClickListenser(listener: (Article) -> Unit){
        onClickListenser = listener
    }


    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}
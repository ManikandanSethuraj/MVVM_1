package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

// Determines the database class
@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDAO

    companion object{

        // Volatile means other threads can easily see when the value of "instance" is changed.
        @Volatile
        private var  instance: ArticleDatabase? = null

        // This is to make sure that there is only one single instance of database
        private val LOCK = Any()

        // instance ?: is called twice which is fine here

        // Synchronized will make sure that all the threads which ever is calling invoke function will not be make it multiple instances of it.
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            // "instance ?:" checks if the instance is null -> if null sets the instance value from createDatabase function
            instance ?: createDatabase(context).also { instance = it }
        }



        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()



    }
}
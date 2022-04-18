package com.example.mldbmovieheaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * class to display items in DB
 */
class ShowDB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_db)

        var db_txt = findViewById<TextView>(R.id.db_dis1)
        db_txt.setText("")

        val db = Room.databaseBuilder(this, MovieDatabase::class.java,
            "tempDBase3").build()

        val movieDao = db.movieDao()

        runBlocking {
            launch {


                val movies: List<Movie> = movieDao.getAll()

                // updating the textView with movie data
                for (u in movies) {
                    db_txt.append("\n ${u.title} \n")
                }

            }
        }
    }
}
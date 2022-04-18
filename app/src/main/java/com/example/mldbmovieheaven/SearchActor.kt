package com.example.mldbmovieheaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchActor : AppCompatActivity() {

    /**
     * creating global variables(class level)
     * edit text to get input
     * actorMovie : to store all the movies of relevant actor
     * textView to display movies
     */
    lateinit var actorInputButton:EditText
    var actorMovie:String = ""
    lateinit var actor_txt:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_actor)

        // initiating views
        actor_txt = findViewById<TextView>(R.id.actor_movie)
        actorInputButton = findViewById<EditText>(R.id.actor_input)

        // button to get searched result. not used
        val searchActorButton = findViewById<Button>(R.id.actor_btn2)

        searchActorButton.setOnClickListener {
            actorMovie=""
            readSearchInput()
            actor_txt.text=actorMovie
        }

        // to restore data after a rotation
        if(savedInstanceState!=null){
            actorMovie = savedInstanceState.getString("last_data").toString()
        }

        // setting text again after a orientation change
        actor_txt.setText(actorMovie)

    }


    /**
     * function to read edit text and display output
     */
    fun readSearchInput(){

        // taking the user input
        var actorName:String = actorInputButton.text.toString()

//        actor_txt.setText("")

        val db = Room.databaseBuilder(this, MovieDatabase::class.java,
            "tempDBase3").build()

        // creating Dao object
        val movieDao = db.movieDao()

        // creating coroutine for database activity
        runBlocking {
            launch {

                // creating movie object List using db query
                val movies: List<Movie> = movieDao.getAllTitles(actorName)

                // extracting movie titles from Movie objects
                for (u in movies) {
                    actorMovie=actorMovie+"\n ${u.title} \n"
                }


            }
        }


    }

    // to save instance state when orientation change happens
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("last_data",actorMovie)
    }

    // to restore instance state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState.getString("last_data",actorMovie)
    }

}

package com.example.mldbmovieheaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchMovie : AppCompatActivity() {

    // declaring views
    lateinit var resultDisplay: TextView
    lateinit var inputData: EditText
    lateinit var web_url:String

    // variables to store movie data
    lateinit var title:String
    lateinit var year:String
    lateinit var rated:String
    lateinit var released:String
    lateinit var movieRuntime:String
    lateinit var genre:String
    lateinit var director:String
    lateinit var writer:String
    lateinit var actors:String
    lateinit var plot:String
    lateinit var movieId:String

    var movieSearchData:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        // initiating views
        resultDisplay = findViewById(R.id.tv)
        val saveButton = findViewById<Button>(R.id.save_btn)
        inputData = findViewById<EditText>(R.id.edit_text1)
        val retrieveButton1 = findViewById<Button>(R.id.ret_movie_btn)

        /**
         * adding functions to buttons
         */
        saveButton.setOnClickListener {
            saveToDatabase()
        }

        retrieveButton1?.setOnClickListener {
            movieRetrieve()
        }

        // restoring instance state
        if(savedInstanceState!=null){
            movieSearchData = savedInstanceState.getString("last_data").toString()
        }

        resultDisplay.setText(movieSearchData)


    }

    /**
     * function to get movie data
     */
    fun movieRetrieve() {
        movieSearchData = ""
        val movieName = inputData!!.text.toString().trim()
        if (movieName == "")
            return

        web_url = "https://www.omdbapi.com/?t=$movieName&apikey=8d75dcb4"

        // coroutine to fetch data from web api
        runBlocking {
            withContext(Dispatchers.IO) {
                // string builder to hold json data
                val stb1 = StringBuilder("")

                // Creating URL object and http connection using URL
                val url = URL(web_url)
                val url_connection = url.openConnection() as HttpURLConnection

                val bfr: BufferedReader
                try {
                    bfr = BufferedReader(InputStreamReader(url_connection.inputStream))
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    return@withContext
                }

                // reading the json and appending the string builder
                var line = bfr.readLine()
                while (line != null) {
                    stb1.append(line)
                    line = bfr.readLine()
                }

                // calling function to extract json data
                movieSearchData=jsonExtractor(stb1)

            }

            // displaying the result in text view
            resultDisplay.setText(movieSearchData)


        }
    }

    /**
     * function to extract data from json
     */
    suspend fun jsonExtractor(stb: java.lang.StringBuilder) :String {

        val json = JSONObject(stb.toString())

        var movieData = java.lang.StringBuilder()


        /**
         * extracting required data from the json object
         */
        title = json["Title"] as String
        movieData.append("\nTitle: "+title)

        year = json["Year"] as String
        movieData.append("\nYear: "+year)

        rated = json["Rated"] as String
        movieData.append("\nRated: "+rated)

        released = json["Released"] as String
        movieData.append("\nReleased: "+released)

        movieRuntime = json["Runtime"] as String
        movieData.append("\nRuntime: "+movieRuntime)

        genre = json["Genre"] as String
        movieData.append("\nGenre: "+genre)

        director = json["Director"] as String
        movieData.append("\nDirector: "+director)

        writer = json["Writer"] as String
        movieData.append("\nWriter: "+writer)

        actors = json["Actors"] as String
        movieData.append("\nActors: "+actors)

        plot = json["Plot"] as String
        movieData.append("\n\nPlot: "+plot)

        movieId = json["imdbID"] as String

        // returning the string builder as a string
        return movieData.toString()


    }

    /**
     * function to save Movie in to database
     */
    fun saveToDatabase() {

        val db = Room.databaseBuilder(
            this, MovieDatabase::class.java,
            "tempDBase3"
        ).build()

        val movieDao = db.movieDao()

        runBlocking {
            launch {

                // inserting movie object using Dao.
                movieDao.insertMovies(Movie(movieId,
                    title,
                    year,
                    rated,
                    released,
                    movieRuntime,
                    genre,
                    director,
                    writer,
                    actors,
                    plot))

            }
        }
    }

    // to save instance state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("last_data",movieSearchData)
    }

    // to restore state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState.getString("last_data",movieSearchData)
    }


}
package com.example.mldbmovieheaven

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchAllMovies : AppCompatActivity() {

    // declaring view variables
    lateinit var showMovie_tv: TextView
    lateinit var inputMovie: EditText
    lateinit var displayMovies: TextView

    // declaring class level variables
    lateinit var web_url: String
    var searchResult:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_all_movies)

        // initiating views
        showMovie_tv = findViewById(R.id.show_movies)
        inputMovie = findViewById(R.id.input3)
        displayMovies = findViewById(R.id.show_movies)

        // button is not used for the searching
        val searchAnyMovie = findViewById<Button>(R.id.search_any_btn)

//        searchAnyMovie.setOnClickListener {
//            movieRetrieve()
//        }


        // creating textWatcher object to get the output when inputting letter by letter
        val textWatcherObj: TextWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            // function to invoke when letter is inserted
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (filterLongEnough()) {
                    movieRetrieve()
                }
            }

            private fun filterLongEnough(): Boolean {
                // searching only if the text length becomes equal or greater to 2
                return inputMovie.text.toString().trim { it <= ' ' }.length > 2
            }
        }

        // adding text change listener to the edit text
        inputMovie.addTextChangedListener(textWatcherObj)

        // retrieving saved instance state data
        if(savedInstanceState!=null){
            searchResult = savedInstanceState.getString("last_data").toString()
        }

        displayMovies.setText(searchResult)


    }

    /**
     * function to get movies data from web api
     */
    fun movieRetrieve() {
        searchResult = ""
        val movieName = inputMovie!!.text.toString().trim()
        if (movieName == "")
            return

        web_url = "https://www.omdbapi.com/?s=$movieName*&apikey=8d75dcb4"

        // coroutine to fetch data from web api
        runBlocking {
            withContext(Dispatchers.IO) {
                // creating string builder to collect json data
                val stb = StringBuilder("")

                // creating url object
                val url = URL(web_url)
                // creating connection using url object
                val url_connection = url.openConnection() as HttpURLConnection

                // creating buffer reader to read json
                val bf: BufferedReader
                try {
                    bf = BufferedReader(InputStreamReader(url_connection.inputStream))
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    return@withContext
                }

                // reading json line by line
                var line = bf.readLine()
                while (line != null) {
                    stb.append(line)
                    line = bf.readLine()
                }

                // calling function to extract json data
                searchResult=jsonExtractor(stb)

            }

            // displaying extracted data on textView
            displayMovies.setText(searchResult)

        }
    }


    /**
     * function to extract data from json data
     */
    suspend fun jsonExtractor(stb: java.lang.StringBuilder): String {

        val json = JSONObject(stb.toString())

        var dataStoreStb = java.lang.StringBuilder()

        // getting data using key Search. json array of search result
        var jsonArray:JSONArray = json.getJSONArray("Search")

        // iterating inside the json array
        for (i in 0..jsonArray.length()-1) {

            // getting each json objects from json array.
            val movie_item: JSONObject = jsonArray[i] as JSONObject

            // getting title of the movie
            val title = movie_item["Title"] as String
            // appending to store data in string builder
            dataStoreStb.append("${i+1}) \"$title\" ")

            dataStoreStb.append("\n\n")
        }

        // returning string builder by converting it to String
        return dataStoreStb.toString()
    }

    /**
     * to save instance state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("last_data",searchResult)
    }

    /**
     * to restore state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState.getString("last_data",searchResult)
    }

}

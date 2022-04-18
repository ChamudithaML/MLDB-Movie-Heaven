package com.example.mldbmovieheaven


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("Select * from movie")
    suspend fun getAll(): List<Movie>

    // query to get movies which has actor name entered by user. LIKE used to get movie name without inputting whole actor name
    @Query("SELECT * FROM movie WHERE actors LIKE '%' || :name || '%'")
    suspend fun getAllTitles(name: String?): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(vararg movie: Movie)

    @Insert
    suspend fun insertAll(vararg movies: Movie)
}

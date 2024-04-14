package com.example.oblig1

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals")
    fun getAllAnimals(): LiveData<List<Animal>>

    @Insert
    fun insertAnimal(animal: Animal)

    @Query("SELECT * FROM animals WHERE photoUri = :uri LIMIT 1")
    fun getAnimalByUri(uri: String): LiveData<Animal?> // Use LiveData to observe changes

    @Query("SELECT * FROM animals ORDER BY RANDOM() LIMIT :count")
    fun getRandomAnimals(count: Int): LiveData<List<Animal>>

    @Query("SELECT * FROM animals ORDER BY name ASC") // Order by name in ascending order
    fun getAllAnimalsSortedByNameAscending(): LiveData<List<Animal>>

    @Query("SELECT * FROM animals ORDER BY name DESC") // Order by name in descending order
    fun getAllAnimalsSortedByNameDescending(): LiveData<List<Animal>>

    @Delete
    fun deleteAnimal(animal: Animal)
}
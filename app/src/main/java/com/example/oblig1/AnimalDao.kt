package com.example.oblig1

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals")
    fun getAllAnimals(): LiveData<List<Animal>>

    @Insert
    fun insertAnimal(animal: Animal)
}
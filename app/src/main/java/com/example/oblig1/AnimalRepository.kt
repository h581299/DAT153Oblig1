package com.example.oblig1

import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutionException

class AnimalRepository(private val animalDao: AnimalDao) {
    val allAnimals: LiveData<List<Animal>> = animalDao.getAllAnimals()

    suspend fun insert(animal: Animal) {
        animalDao.insertAnimal(animal)
    }
}
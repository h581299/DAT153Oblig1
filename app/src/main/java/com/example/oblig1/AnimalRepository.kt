package com.example.oblig1

import androidx.lifecycle.LiveData

class AnimalRepository(private val animalDao: AnimalDao) {
    val allAnimals: LiveData<List<Animal>> = animalDao.getAllAnimals()

    suspend fun insert(animal: Animal) {
        animalDao.insertAnimal(animal)
    }
}
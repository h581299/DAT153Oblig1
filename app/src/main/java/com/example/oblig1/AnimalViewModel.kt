package com.example.oblig1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnimalViewModel(private val repository: AnimalRepository) : ViewModel() {
    val allAnimals: LiveData<List<Animal>> = repository.allAnimals

    suspend fun insert(animal: Animal) {
        repository.insert(animal)
    }
}

class AnimalViewModelFactory(private val repository: AnimalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
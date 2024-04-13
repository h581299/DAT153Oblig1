package com.example.oblig1

import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.oblig1.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.oblig1.AnimalDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AnimalViewModel
    private lateinit var animalDao: AnimalDao
    private lateinit var animalDatabase: AnimalDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AnimalDatabase instance
        animalDatabase = AnimalDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.Main))

        // Get AnimalDao instance from AnimalDatabase
        animalDao = animalDatabase.animalDao()

        val repository = AnimalRepository(animalDao)

        // Initialize AnimalData
        viewModel = ViewModelProvider(this, AnimalViewModelFactory(repository)).get(AnimalViewModel::class.java)

        // Observe the list of animals
        viewModel.allAnimals.observe(this, Observer { animals ->
            // Update your UI here with the list of animals
            // You can initialize your AnimalData object here with the observed data
            animals.forEach { animal ->
                AnimalData.animals.add(Pair(Uri.parse(animal.photoUri), animal.name))
            }
        })
    }

    // Navigation button to GalleryActivity
    fun onGalleryButtonClick(view: View) {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    // Navigation button to QuizActivity
    fun onQuizButtonClick(view: View) {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }

    private fun getResourceUri(imageResId: Int): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + resources.getResourcePackageName(imageResId)
                + '/' + resources.getResourceTypeName(imageResId)
                + '/' + resources.getResourceEntryName(imageResId))
    }
}
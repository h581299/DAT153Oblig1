package com.example.oblig1

import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AnimalData
        AnimalData.animals.apply {
            add(Pair(getResourceUri(R.drawable.cat), "Cat"))
            add(Pair(getResourceUri(R.drawable.dog), "Dog"))
            add(Pair(getResourceUri(R.drawable.opossum), "Opossum"))
        }
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
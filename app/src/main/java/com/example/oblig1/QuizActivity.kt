package com.example.oblig1

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.InputStream

class QuizActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var scoreTextView: TextView

    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        imageView = findViewById(R.id.imageView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        scoreTextView = findViewById(R.id.scoreTextView)

        displayNextQuestion()

    }

    private fun displayNextQuestion() {
        // Randomly select a photo from gallery
        val randomEntry = AnimalData.animals.random()
        val imageUri = randomEntry.first
        val correctName = randomEntry.second

        Log.d("MyTag", "$imageUri")

        // Set the photo in the ImageView
        if (imageUri.scheme == ContentResolver.SCHEME_ANDROID_RESOURCE) {
            // URI represents a resource ID
            val resourceId = resources.getIdentifier(imageUri.lastPathSegment, "drawable", packageName)
            Log.d("MyTag2", "$resourceId")
            imageView.setImageResource(resourceId)
        } else {
            // URI represents a file or content URI
            Log.d("MyTag3", "mytag3")
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        }

        // Get three random names (one right and two wrong)
        val options = mutableListOf(correctName)
        val uniqueNames = AnimalData.animals.map { it.second }.distinct()
        if (uniqueNames.size >= 3) {
            val shuffledNames = uniqueNames.shuffled().take(3)
            options.addAll(shuffledNames.filter { it != correctName }.take(2))
        }

        options.shuffle()

        option1Button.text = options[0]
        option2Button.text = options[1]
        option3Button.text = options[2]

        option1Button.setOnClickListener { checkAnswer(option1Button.text.toString(), correctName) }
        option2Button.setOnClickListener { checkAnswer(option2Button.text.toString(), correctName) }
        option3Button.setOnClickListener { checkAnswer(option3Button.text.toString(), correctName) }
    }

    private fun checkAnswer(selectedName: String, correctName: String) {
        if (selectedName == correctName) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect! The correct name is $correctName", Toast.LENGTH_SHORT).show()
        }
        updateScore()
        displayNextQuestion()
    }

    private fun updateScore() {
        scoreTextView.text = "Score: $score"
    }
}

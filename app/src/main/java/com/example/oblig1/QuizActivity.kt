package com.example.oblig1

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.InputStream

class QuizActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var scoreTextView: TextView

    private lateinit var animalDao: AnimalDao

    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        imageView = findViewById(R.id.imageView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        scoreTextView = findViewById(R.id.scoreTextView)

        // Initialize AnimalDatabase instance and obtain AnimalDao
        val animalDatabase = AnimalDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.Main))
        animalDao = animalDatabase.animalDao()

        fetchNextQuestion()
    }

    private fun fetchNextQuestion() {
        animalDao.getRandomAnimals(1).observe(this, Observer { animals ->
            animals.firstOrNull()?.let { animal ->
                displayNextQuestion(animal)
            }
        })
    }

    private fun displayNextQuestion(currentAnimal: Animal) {
        // Set the photo in the ImageView
        if (currentAnimal.photoUri.startsWith("drawable")) {
            val resourceId = resources.getIdentifier(currentAnimal.photoUri, "drawable", packageName)
            imageView.setImageResource(resourceId)
            imageView.tag = currentAnimal.name
        } else {
            val inputStream: InputStream? = contentResolver.openInputStream(Uri.parse(currentAnimal.photoUri))
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            imageView.tag = currentAnimal.name
        }

        // Get three random names (one right and two wrong)
        val options = mutableListOf(currentAnimal.name)
        val uniqueNames = mutableListOf<String>()

        // Get three animals to add to the option buttons with one being the correct animal
        animalDao.getAllAnimals().observe(this, Observer { animals ->
            uniqueNames.clear()
            uniqueNames.addAll(animals.map { it.name }.distinct())

            if (uniqueNames.size >= 3) {
                val wrongNames = uniqueNames.filter { it != currentAnimal.name }.shuffled().take(2)
                options.addAll(wrongNames)
                options.shuffle()

                option1Button.text = options[0]
                option2Button.text = options[1]
                option3Button.text = options[2]
            }
        })

        // Set onClickListener for each button to call checkAnswer with their assigned animal and the correct answer
        option1Button.setOnClickListener { checkAnswer(option1Button.text.toString(), currentAnimal.name) }
        option2Button.setOnClickListener { checkAnswer(option2Button.text.toString(), currentAnimal.name) }
        option3Button.setOnClickListener { checkAnswer(option3Button.text.toString(), currentAnimal.name) }
    }

    fun checkAnswer(selectedName: String, correctName: String) {
        val rootView = findViewById<View>(R.id.constraintLayout) // Assuming the id of the ConstraintLayout is constraintLayout

        // Check if the answer was wrong and increaase the score if it was.
        if (selectedName == correctName) {
            score++
            Snackbar.make(rootView, "Correct!", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(rootView, "Wrong!", Snackbar.LENGTH_SHORT).show()
        }

        updateScore()

        fetchNextQuestion() // Fetch the next question

    }

    // Update score display accordingly to current score
    private fun updateScore() {
        this@QuizActivity.runOnUiThread {
            this@QuizActivity.scoreTextView.text = "Score: $score"
        }
    }
}

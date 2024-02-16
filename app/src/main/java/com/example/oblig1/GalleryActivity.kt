package com.example.oblig1

import android.app.Activity
import android.content.Intent

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
        private const val REQUEST_CODE_SHOW_IMAGE = 1002
    }

    private var enteredName: String? = null
    private var isSortedAscending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        createGalleryView(AnimalData.animals)
    }

    fun createGalleryView(animalData: MutableList<Pair<Uri, String>>) {
        // Initialize the gallery layout
        val galleryLayout: LinearLayout = findViewById(R.id.galleryLayout)

        // Create gallery view with animal data
        for ((photoResId, name) in animalData) {
            // Create LinearLayout to hold the ImageView and TextView
            val itemLayout = LinearLayout(this)
            itemLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemLayout.orientation = LinearLayout.VERTICAL
            itemLayout.setPadding(16, 16, 16, 16)

            // Create and configure ImageView
            val photoImageView = ImageView(this)
            photoImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Load the bitmap from the URI using ContentResolver
            val inputStream = contentResolver.openInputStream(photoResId)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Set the bitmap into the ImageView
            photoImageView.setImageBitmap(bitmap)

            // Styling calculation
            photoImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels
            val imageViewHeight = screenHeight / 2

            // Set layout parameters for the ImageView to make it 50% of the screen height
            photoImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                imageViewHeight
            )

            // Create and configure TextView
            val nameTextView = TextView(this)
            nameTextView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            nameTextView.text = name
            nameTextView.setTextColor(Color.BLACK)
            nameTextView.textSize = 36f
            nameTextView.gravity = Gravity.CENTER

            // Add ImageView and TextView to the itemLayout
            itemLayout.addView(photoImageView)
            itemLayout.addView(nameTextView)

            // Add the itemLayout to the galleryLayout
            galleryLayout.addView(itemLayout)
        }
    }

    fun onSortButtonClick(view: View) {
        val galleryLayout: LinearLayout = findViewById(R.id.galleryLayout)

        // Sort animal data alphabetically based on names
        AnimalData.animals.sortBy { it.second }

        // Reverse the sorting order if already sorted
        if (!isSortedAscending) {
            AnimalData.animals.reverse()
        }

        // Update the gallery view
        galleryLayout.removeAllViews()
        setContentView(R.layout.activity_gallery)
        createGalleryView(AnimalData.animals)

        // Toggle sorting order flag
        isSortedAscending = !isSortedAscending
    }

    fun onAddEntryButtonClick(view: View) {
        showNameInputDialog()
    }

    private fun showNameInputDialog() {
        val inputEditText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Name")
            .setView(inputEditText)
            .setPositiveButton("OK") { dialog, which ->
                val name = inputEditText.text.toString()
                addNewEntry(name)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    fun addNewEntry(name: String) {
        // Create an intent to open the device's file system
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val name = data?.getStringExtra("name")
            if (imageUri != null) {
                // Add the content URI and name to AnimalData
                AnimalData.animals.add(Pair(imageUri, name?: "Jerry"))

                val galleryLayout: LinearLayout = findViewById(R.id.galleryLayout)
                galleryLayout.removeAllViews()
                setContentView(R.layout.activity_gallery)
                createGalleryView(AnimalData.animals)
            }
        }
    }

}

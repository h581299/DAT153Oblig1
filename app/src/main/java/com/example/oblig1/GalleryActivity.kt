package com.example.oblig1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.net.Uri
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


class GalleryActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001 // or any value you prefer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Access the animals list from AnimalData
        val animalData = AnimalData.animals

        createGalleryView(animalData)
    }

    fun createGalleryView(animalData: MutableList<Pair<Uri, String>> ) {
        // Initialize the gallery layout
        val galleryLayout: LinearLayout = findViewById(R.id.galleryLayout)

        // We go through each entry in the animalData and adds them programmatically
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

            photoImageView.scaleType = ImageView.ScaleType.CENTER_CROP // Adjust image scale type as needed

            // Calculate the desired height for the ImageView (50% of screen height)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels
            val imageViewHeight = screenHeight / 2

            // Set layout parameters for the ImageView to make it 50% of the screen height
            photoImageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageViewHeight)

            // Attach OnClickListener to the dynamically created ImageView
            photoImageView.setOnClickListener {
                // Remove the corresponding entry from AnimalData
                val index = galleryLayout.indexOfChild(it.parent as View)
                AnimalData.animals.removeAt(index)

                // Remove the clicked ImageView from the galleryLayout
                galleryLayout.removeView(it.parent as View)
            }

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

    fun onAddEntryButtonClick(view: View) {
        // Open a dialog or input field to prompt the user to enter the name for the new entry
        // For simplicity, let's assume you have a method called showNameInputDialog() for this purpose
        showNameInputDialog()
    }

    private fun showNameInputDialog() {
        val inputEditText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Name")
            .setView(inputEditText)
            .setPositiveButton("OK") { dialog, which ->
                val name = inputEditText.text.toString()
                // Call the method to launch the image picker with the provided name
                addNewEntry(name)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    fun addNewEntry(name: String) {
        // Create an intent to open the device's file system or gallery
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val contentResolver = contentResolver
                val cursor = contentResolver.query(imageUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val imageName = it.getString(nameIndex)
                        // Add the new entry to AnimalData with the image URI and name
                        AnimalData.animals.add(Pair(imageUri, imageName))

                        // Update the gallery view to display the new entry
                        createGalleryView(AnimalData.animals)
                    }
                }
            }
        }
    }

}

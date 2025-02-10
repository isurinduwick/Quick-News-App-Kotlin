package com.example.newsagencyproject

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class PulisherActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var txtTopic: EditText
    private lateinit var txtDescription: EditText

    private val REQUEST_IMAGE_CAPTURE = 100
    private val REQUEST_GALLERY_SELECT = 200
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_pulisher)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView2)
        val buttonCamera = findViewById<AppCompatImageButton>(R.id.btnCamera)
        val buttonGallery = findViewById<AppCompatImageButton>(R.id.btnGallery)
        btnSubmit = findViewById(R.id.btnSubmit)
        txtTopic = findViewById(R.id.txtTopic)
        txtDescription = findViewById(R.id.txtDescription)

        // Initialize Cloudinary
        initCloudinary()

        // Open Camera
        buttonCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Open Gallery
        buttonGallery.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_SELECT)
        }

        // Submit Data
        btnSubmit.setOnClickListener {
            if (imageUri != null) {
                uploadImageToCloudinary()
            } else {
                Toast.makeText(this, "Please capture or select an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val file = saveImageLocally(imageBitmap)
                    imageUri = Uri.fromFile(file)
                    imageView.setImageBitmap(imageBitmap)
                }
                REQUEST_GALLERY_SELECT -> {
                    imageUri = data?.data
                    imageView.setImageURI(imageUri)
                }
            }
        }
    }

    private fun saveImageLocally(bitmap: Bitmap): File {
        val file = File(filesDir, "image_${UUID.randomUUID()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            Log.e("PulisherActivity", "Error saving image: ${e.message}")
        }
        return file
    }






    private fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "db0gn7yje",
            "api_key" to "495231318915193",
            "api_secret" to "4EiiNqrhKmXR7BoxOpv-f1xVzTU"
        )
        MediaManager.init(this, config)
    }

    private fun uploadImageToCloudinary() {
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()
        MediaManager.get().upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"].toString()
                    uploadData(imageUrl)
                }

                override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                    Toast.makeText(this@PulisherActivity, "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                    Toast.makeText(this@PulisherActivity, "Upload rescheduled", Toast.LENGTH_SHORT).show()
                }
            }).dispatch()
    }

    private fun uploadData(imageUrl: String) {
        val title = txtTopic.text.toString()
        val desc = txtDescription.text.toString()

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val currentData = System.currentTimeMillis().toString()

        val dataClass = DataClass(imageUrl, title, desc, "Pending", userEmail)

        FirebaseDatabase.getInstance().getReference("News Reports")
            .child(currentData)
            .setValue(dataClass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@PulisherActivity, "Submitted successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@PulisherActivity, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

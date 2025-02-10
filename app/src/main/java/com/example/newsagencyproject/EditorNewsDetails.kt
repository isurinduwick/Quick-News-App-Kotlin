package com.example.newsagencyproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditorNewsDetails : AppCompatActivity() {
    private lateinit var newsImage: ImageView
    private lateinit var newsTitle: TextView
    private lateinit var newsDescription: TextView
    private lateinit var newsStatus: TextView
    private lateinit var btnApprove: Button
    private lateinit var btnReject: Button

    private lateinit var database: DatabaseReference
    private var newsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_news_details)

        newsImage = findViewById(R.id.newsImage)
        newsTitle = findViewById(R.id.newsTitle)
        newsDescription = findViewById(R.id.newsDescription)
        newsStatus = findViewById(R.id.newsStatus)
        btnApprove = findViewById(R.id.btnApprove)
        btnReject = findViewById(R.id.btnReject)

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("news")

        // Get data from intent
        newsId = intent.getStringExtra("newsId")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("image")
        val status = intent.getStringExtra("status")

        // Set data to UI
        newsTitle.text = title
        newsDescription.text = description
        newsStatus.text = status

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(newsImage)
        }

        // Set status color
        updateStatusUI(status)

        btnApprove.setOnClickListener {
            updateNewsStatus("Approved")
        }

        btnReject.setOnClickListener {
            updateNewsStatus("Rejected")
        }
    }

    private fun updateNewsStatus(newStatus: String) {
        newsId?.let {
            database.child(it).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    updateStatusUI(newStatus)
                    Toast.makeText(this, "News marked as $newStatus", Toast.LENGTH_SHORT).show()

                    // Send result back to EditorActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("newsId", newsId)
                    resultIntent.putExtra("status", newStatus)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // Close activity after update
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateStatusUI(status: String?) {
        newsStatus.text = status
        when (status) {
            "Approved" -> newsStatus.setBackgroundResource(R.drawable.status_active)
            "Pending" -> newsStatus.setBackgroundResource(R.drawable.status_pending)
            "Rejected" -> newsStatus.setBackgroundResource(R.drawable.status_inactive)
        }
    }
}

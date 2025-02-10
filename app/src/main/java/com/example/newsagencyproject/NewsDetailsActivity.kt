package com.example.newsagencyproject

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class NewsDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve data from intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("imageUrl")

        // Find views
        val titleTextView: TextView = findViewById(R.id.newsTitle)
        val descriptionTextView: TextView = findViewById(R.id.newsDescription)
        val newsImageView: ImageView = findViewById(R.id.newsImage)

        // Set values to views
        titleTextView.text = title ?: "No Title Available"
        descriptionTextView.text = description ?: "No Description Available"

        // Load image using Glide
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.d)
                .into(newsImageView)
        } else {
            newsImageView.setImageResource(R.drawable.placeholder_image)
        }
    }
}

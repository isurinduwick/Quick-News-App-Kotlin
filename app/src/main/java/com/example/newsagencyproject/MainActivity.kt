package com.example.newsagencyproject

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsList: MutableList<DataClass>  // Mutable list since we will be adding to it
    private lateinit var adapter: DashboardAdapter
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Initialize Cloudinary
        CloudinaryManager.init(this)


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnlogin = findViewById<AppCompatImageButton>(R.id.fab
        )

        btnlogin.setOnClickListener() {

                val intent = Intent(this, PulisherActivity::class.java)
                startActivity(intent)
                finish()

        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("News Reports")
        // Initialize the news list
        newsList = mutableListOf()
        // Initialize the adapter with the news list
        adapter = DashboardAdapter(newsList)
        recyclerView.adapter = adapter
        // Fetch data from Firebase
        fetchNewsData()

    }
    private fun fetchNewsData() {
        // Retrieve data from Firebase Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the list before adding new data
                newsList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val newsItem = dataSnapshot.getValue(DataClass::class.java)
                        if (newsItem != null) {
                            newsList.add(newsItem)
                        }
                    }
                    Log.d("Firebase", "Data fetched successfully: ${newsList.size} items")
                } else {
                    Log.d("Firebase", "No data found at the path")
                }

                // Notify the adapter that data has changed
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Failed to load news: ${error.message}")
                Toast.makeText(applicationContext, "Failed to load news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


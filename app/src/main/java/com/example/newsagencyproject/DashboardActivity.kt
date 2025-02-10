package com.example.newsagencyproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsList: MutableList<DataClass>
    private lateinit var adapter: DashboardAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin = findViewById<AppCompatImageButton>(R.id.fab)
        btnLogin.setOnClickListener {
            val intent = Intent(this, PulisherActivity::class.java)
            startActivity(intent)
            finish()
        }
        val btnLogout = findViewById<MaterialButton>(R.id.buttonLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("News Reports")
        newsList = mutableListOf()
        adapter = DashboardAdapter(newsList)
        recyclerView.adapter = adapter

        fetchUserNews()
    }

    private fun fetchUserNews() {
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val newsItem = dataSnapshot.getValue(DataClass::class.java)
                        if (newsItem != null && newsItem.email == currentUserEmail) {
                            newsList.add(newsItem)
                        }
                    }
                    Log.d("Firebase", "Data fetched successfully: ${newsList.size} items")
                } else {
                    Log.d("Firebase", "No data found for current user")
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to load news: ${error.message}")
                Toast.makeText(applicationContext, "Failed to load news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

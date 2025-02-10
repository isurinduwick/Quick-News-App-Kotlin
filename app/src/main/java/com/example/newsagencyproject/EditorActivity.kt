package com.example.newsagencyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*

class EditorActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsList: MutableList<Pair<String, DataClass>> // Pair<UniqueKey, NewsData>
    private lateinit var adapter: EditorAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val btnLogout = findViewById<MaterialButton>(R.id.buttonLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        newsList = mutableListOf()
        adapter = EditorAdapter(this, newsList, FirebaseDatabase.getInstance().reference.child("News Reports"))
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("News Reports")

        fetchPendingNews()
    }

    private fun fetchPendingNews() {
        database.orderByChild("status").equalTo("Pending") // Get only pending news
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    newsList.clear()
                    for (newsSnapshot in snapshot.children) {
                        val newsItem = newsSnapshot.getValue(DataClass::class.java)
                        if (newsItem != null) {
                            val uniqueKey = newsSnapshot.key.toString() // Get Firebase unique key
                            newsList.add(Pair(uniqueKey, newsItem))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditorActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

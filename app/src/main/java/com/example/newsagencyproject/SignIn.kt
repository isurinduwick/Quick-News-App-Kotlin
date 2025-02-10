package com.example.newsagencyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.newsagencyproject.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Navigate to Sign Up screen
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Login button click
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth.currentUser?.uid
                        if (uid != null) {
                            checkUserRole(uid)  // Check user role after successful login
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserRole(uid: String) {
        val database = FirebaseDatabase.getInstance().getReference("Users").child(uid)

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val role = snapshot.child("role").value.toString()

                when (role) {
                    "reporter" -> {
                        startActivity(Intent(this, DashboardActivity::class.java))  // Redirect to Reporter Dashboard
                    }
                    "editor" -> {
                        startActivity(Intent(this, EditorActivity::class.java))  // Redirect to Editor Dashboard
                    }
                    else -> {
                        Toast.makeText(this, "Invalid role assigned", Toast.LENGTH_SHORT).show()
                    }
                }
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
        }
    }
}

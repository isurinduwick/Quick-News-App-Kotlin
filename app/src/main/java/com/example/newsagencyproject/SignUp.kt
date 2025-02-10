package com.example.newsagencyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import com.example.newsagencyproject.SignIn
import com.example.newsagencyproject.databinding.ActivitySignUpBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Sign Up button click event
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Create a new user with Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User registration successful, now save the role in Firebase
                            val role = if (binding.radioReporter.isChecked) "reporter" else "editor"
                            saveUserToDatabase(firebaseAuth.currentUser?.uid, email, role)
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(uid: String?, email: String, role: String) {
        if (uid != null) {
            val userMap = mapOf("email" to email, "role" to role)
            database.child(uid).setValue(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignIn::class.java))  // Redirect to SignIn
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

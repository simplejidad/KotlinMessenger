package com.santiaprende.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santiaprende.kotlinmessenger.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    auth = Firebase.auth

    binding.buttonLoginLogin.setOnClickListener {
      val email = binding.edittextEmailLogin.text.toString()
      val password = binding.edittextPasswordLogin.text.toString()

      Log.d("LoginActivity", "Attempt login with email/password: $email/***")

      auth.signInWithEmailAndPassword(email, password)
        //.addOnCompleteListener()
    }

    binding.textviewBackLogin.setOnClickListener {
      finish()
    }
  }
}
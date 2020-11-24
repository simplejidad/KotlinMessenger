package com.santiaprende.kotlinmessenger.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santiaprende.kotlinmessenger.R
import com.santiaprende.kotlinmessenger.databinding.ActivityLoginBinding
import com.santiaprende.kotlinmessenger.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    auth = Firebase.auth

    binding.buttonLoginLogin.setBackgroundResource(R.drawable.rounded_button)

    binding.buttonLoginLogin.setOnClickListener {
      val email = binding.edittextEmailLogin.text.toString()
      val password = binding.edittextPasswordLogin.text.toString()

      Log.d("LoginActivity", "Attempt login with email/password: $email/***")

      auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) {
          val intent = Intent(this, LatestMessagesActivity::class.java)
          startActivity(intent)
          finish()
        }
        .addOnFailureListener {
          Toast.makeText(baseContext, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    binding.textviewBackLogin.setOnClickListener {
      finish()
    }
  }
}
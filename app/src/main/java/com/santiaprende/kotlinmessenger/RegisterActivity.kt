package com.santiaprende.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santiaprende.kotlinmessenger.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding
  private lateinit var auth: FirebaseAuth

  companion object {
    public const val TAG = "RegisterActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)

    auth = Firebase.auth

    binding.buttonRegisterRegister.setBackgroundResource(R.drawable.rounded_button)
    binding.buttonSelectphotoRegister.setBackgroundResource(R.drawable.round_button)

    binding.buttonRegisterRegister.setOnClickListener {
      performRegister()
    }

    binding.textViewAlreadyhaveRegister.setOnClickListener {
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
    }
  }

  private fun performRegister() {
    val email = binding.edittextEmailRegister.text.toString()
    val password = binding.edittextPasswordRegister.text.toString()

    if(email.isEmpty() || password.isEmpty()) {
      Toast.makeText(baseContext,"Please enter email and password", Toast.LENGTH_LONG).show()
      Log.d(TAG, "Empty email or password")
      return
    }

    auth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          val user = auth.currentUser
          Log.d(TAG, "Succesfully created user with uid: $user.uid")
          Toast.makeText(baseContext, "User Created", Toast.LENGTH_SHORT).show()
        } else {
          // If sign in fails, display a message to the user.
          Log.d(TAG, "Failed to create user: ${task.exception?.message}")
          Toast.makeText(baseContext, "Failed to create user ${task.exception?.message}",
            Toast.LENGTH_SHORT).show()
        }
      }
  }
}
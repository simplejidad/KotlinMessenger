package com.santiaprende.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.santiaprende.kotlinmessenger.messages.LatestMessagesActivity
import com.santiaprende.kotlinmessenger.R
import com.santiaprende.kotlinmessenger.databinding.ActivityRegisterBinding
import com.santiaprende.kotlinmessenger.models.User
import java.util.*

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding
  private lateinit var auth: FirebaseAuth

  var selectedPhotoUri : Uri? = null

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

    binding.buttonSelectphotoRegister.setOnClickListener {
      Log.d(TAG, "Try to show photo selector")

      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      Log.d(TAG, "Photo was selected")

      selectedPhotoUri = data.data
      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

      binding.circleImageviewImageRegister.setImageBitmap(bitmap)
      binding.buttonSelectphotoRegister.alpha = 0f
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
          uploadImageToFirebaseStorage()
        } else {
          // If sign in fails, display a message to the user.
          Log.d(TAG, "Failed to create user: ${task.exception?.message}")
          Toast.makeText(baseContext, "Failed to create user ${task.exception?.message}",
            Toast.LENGTH_SHORT).show()
        }
      }
  }

  private fun uploadImageToFirebaseStorage() {
    if (selectedPhotoUri == null) return
    val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    ref.putFile(selectedPhotoUri!!)
      .addOnSuccessListener {
        Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

        ref.downloadUrl.addOnSuccessListener {
          Log.d(TAG, "File Location: $it")

          saveUserToFirebaseDatabase(it.toString())
        }
      }
  }

  private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
    val uid = FirebaseAuth.getInstance().uid ?: ""
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val user = User(uid, binding.edittextUsernameRegister.text.toString(), profileImageUrl )
    ref.setValue(user)
      .addOnSuccessListener {
        Log.d(TAG, "Finally we saved the user to Firebase Database")

        var intent = Intent(this, LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

      }
      .addOnFailureListener {
        Log.d(TAG, "Failed to add user to databaase: ${it.message}")
      }
  }
}


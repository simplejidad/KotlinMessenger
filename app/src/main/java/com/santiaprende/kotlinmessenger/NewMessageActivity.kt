package com.santiaprende.kotlinmessenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.santiaprende.kotlinmessenger.databinding.ActivityNewMessageBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class NewMessageActivity : AppCompatActivity() {

  private lateinit var binding: ActivityNewMessageBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityNewMessageBinding.inflate(layoutInflater)
    setContentView(R.layout.activity_new_message)

    supportActionBar?.title = "Select User"

    fetchUsers()
  }

  private fun fetchUsers() {
    var ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        val adapter = GroupAdapter<GroupieViewHolder>()

        snapshot.children.forEach {
          val user = it.getValue(User::class.java)
          if (user != null) {
            adapter.add(UserItem(user))
          }
        }

        var recyclerView = findViewById<RecyclerView>(R.id.recyclerview_new_message)
        recyclerView.adapter = adapter
      }

      override fun onCancelled(error: DatabaseError) {
        //...
      }
    })
  }
}

class UserItem(val user: User) : Item() {
  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.itemView.textview_username_new_message_row.text = user.username
    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.circleImageview_image_new_user_row)
  }

  override fun getLayout(): Int {
    return R.layout.user_row_new_message
  }
}
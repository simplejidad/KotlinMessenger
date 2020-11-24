package com.santiaprende.kotlinmessenger.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.santiaprende.kotlinmessenger.R
import com.santiaprende.kotlinmessenger.models.ChatMessage
import com.santiaprende.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageItem(var chatMessage: ChatMessage) : Item() {

  var contactUser: User? = null

  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.itemView.textview_message_latest_messages.text = chatMessage.text

    var contactUserId : String? = null
    contactUserId = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
      chatMessage.toId
    } else {
      chatMessage.fromId
    }

    var ref = FirebaseDatabase.getInstance().getReference("users/$contactUserId")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        contactUser = snapshot.getValue(User::class.java)
        viewHolder.itemView.textview_username_latest_messages.text = contactUser?.username
        var targetImageView = viewHolder.itemView.imageview_latest_message_row
        Picasso.get().load(contactUser?.profileImageUrl).into(targetImageView)
      }
      override fun onCancelled(error: DatabaseError) {
      }
    })
  }

  override fun getLayout(): Int {
    return R.layout.latest_message_row
  }
}
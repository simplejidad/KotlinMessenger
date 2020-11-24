package com.santiaprende.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.santiaprende.kotlinmessenger.R
import com.santiaprende.kotlinmessenger.databinding.ActivityChatLogBinding
import com.santiaprende.kotlinmessenger.messages.NewMessageActivity.Companion.USER_KEY
import com.santiaprende.kotlinmessenger.models.ChatMessage
import com.santiaprende.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.message_in_row.view.*
import kotlinx.android.synthetic.main.message_out_row.view.*
import kotlinx.android.synthetic.main.message_out_row.view.textView

class ChatLogActivity : AppCompatActivity() {

  companion object{
    val TAG = "ChatLogActivity"
  }

  val adapter = GroupAdapter<GroupieViewHolder>()

  var contactUser : User? = null

  private lateinit var binding: ActivityChatLogBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityChatLogBinding.inflate(layoutInflater)
    setContentView(binding.root)

    contactUser = intent.getParcelableExtra<User>(USER_KEY)
    supportActionBar?.title = contactUser?.username

    val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_messages_chatlog)
    recyclerView.adapter = adapter

    binding.sendButtonChatlog.setBackgroundResource(R.drawable.rounded_button)

    binding.sendButtonChatlog.setOnClickListener {
      Log.d(TAG, "Try to send message")
      sendMessage()
    }

    listenForMessages()
  }

  private fun listenForMessages() {
    val contactUserId = contactUser?.uid
    val currentUserId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$currentUserId/$contactUserId/")
    ref.addChildEventListener(object: ChildEventListener{
      override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
          adapter.add(MessageOutItem(chatMessage.text, LatestMessagesActivity.currentUser!!))
        } else {
          adapter.add(MessageInItem(chatMessage.text, contactUser!!))
        }
      }

      override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
      }

      override fun onChildRemoved(snapshot: DataSnapshot) {
      }

      override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
      }

      override fun onCancelled(error: DatabaseError) {
      }
    })
  }

  private fun sendMessage() {
    val currentUserId = FirebaseAuth.getInstance().uid ?: return
    val contactId = contactUser!!.uid
    val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$currentUserId/$contactId").push()
    val toRef = FirebaseDatabase.getInstance().getReference("/user_messages/$contactId/$currentUserId").push()
    val latestMessagesRef = FirebaseDatabase.getInstance().getReference("/latest_messages/$currentUserId/$contactId")
    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest_messages/$contactId/$currentUserId")

    val chatMessage = ChatMessage(ref.key!!, binding.edittextMessageChatlog.text.toString(), currentUserId,
      contactId, System.currentTimeMillis() / 1000)

    ref.setValue(chatMessage)
    toRef.setValue(chatMessage)
    latestMessagesRef.setValue(chatMessage)
    latestMessageToRef.setValue(chatMessage)

    binding.edittextMessageChatlog.text.clear()
    binding.recyclerviewMessagesChatlog.scrollToPosition(adapter.itemCount - 1)

  }
}

class MessageInItem(val text: String, val user: User) : Item() {
  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.itemView.textView.text = text
    var imageView = viewHolder.itemView.imageview_message_in_row
    Picasso.get().load(user.profileImageUrl).into(imageView)
  }

  override fun getLayout(): Int {
    return R.layout.message_in_row
  }

}

class MessageOutItem(val text: String, val user: User) : Item() {
  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.itemView.textView.text = text
    var imageView = viewHolder.itemView.imageview_message_out_row
    Picasso.get().load(user.profileImageUrl).into(imageView)
  }

  override fun getLayout(): Int {
    return R.layout.message_out_row
  }
}
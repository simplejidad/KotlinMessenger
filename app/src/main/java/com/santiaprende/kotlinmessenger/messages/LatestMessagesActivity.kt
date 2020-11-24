package com.santiaprende.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.santiaprende.kotlinmessenger.R
import com.santiaprende.kotlinmessenger.messages.NewMessageActivity.Companion.USER_KEY
import com.santiaprende.kotlinmessenger.models.ChatMessage
import com.santiaprende.kotlinmessenger.models.User
import com.santiaprende.kotlinmessenger.registerlogin.RegisterActivity
import com.santiaprende.kotlinmessenger.views.LatestMessageItem
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.latest_message_row.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class LatestMessagesActivity : AppCompatActivity() {
  companion object {
    var currentUser : User? = null
  }

  val adapter = GroupAdapter<GroupieViewHolder>()

  var latestMessagesMap = HashMap<String, ChatMessage>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_latest_messages)

    verifyIfUserIsLoggedIn()

    getFromUser()

    val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_latest_messages)
    recyclerView.adapter = adapter

    adapter.setOnItemClickListener { item, view ->
      val intent = Intent(this, ChatLogActivity::class.java)
      val latestMessageItem = item as LatestMessageItem
      intent.putExtra(USER_KEY, latestMessageItem.contactUser)
      startActivity(intent)
    }
  }

  private fun refreshRecyclerViewMessages() {
    adapter.clear()
    latestMessagesMap.values.forEach {
      adapter.add(LatestMessageItem(it))
    }
  }

  private fun listenForLatestMessages() {
    val currentUserId = currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$currentUserId")
    ref.addChildEventListener(object: ChildEventListener {
      override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
        latestMessagesMap[snapshot.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }

      override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
        latestMessagesMap[snapshot.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }

      override fun onChildRemoved(snapshot: DataSnapshot) {
      }

      override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
      }

      override fun onCancelled(error: DatabaseError) {
      }
    })
  }

  private fun getFromUser() {
    var userId = FirebaseAuth.getInstance().uid
    var ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        currentUser = snapshot.getValue(User::class.java)
        listenForLatestMessages()
      }

      override fun onCancelled(error: DatabaseError) {

      }
    })
  }

  private fun verifyIfUserIsLoggedIn() {
    var uid = FirebaseAuth.getInstance().uid
    if (uid == null) {
      var intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    var inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.nav_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.new_message -> {
        var intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
      }
      R.id.log_out -> {
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
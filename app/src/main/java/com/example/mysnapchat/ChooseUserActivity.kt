package com.example.mysnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChooseUserActivity : AppCompatActivity() {

    var listViewChooseUser : ListView? = null
    var emailsList : ArrayList<String> = ArrayList()
    var keysList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        listViewChooseUser = findViewById(R.id.listViewChooseUser)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emailsList)
        listViewChooseUser?.adapter = adapter

        FirebaseDatabase.getInstance().reference
                .child("users").addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val email = snapshot.child("email").value as String
                        if (email != Firebase.auth.currentUser?.email){
                            emailsList.add(email)
                            keysList.add(snapshot.key!!)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                })

        listViewChooseUser?.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, id ->

                    val snapMap : Map<String, String> =
                            mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,
                                "imageName" to intent.getStringExtra("imageName")!!,
                                "imageUrl" to intent.getStringExtra("imageUrl")!!,
                                "message" to intent.getStringExtra("message")!!)

                    FirebaseDatabase.getInstance().reference
                            .child("users").child(keysList.get(position))
                            .child("snaps").push().setValue(snapMap)

                    val intent = Intent(this, SnapsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
    }
}
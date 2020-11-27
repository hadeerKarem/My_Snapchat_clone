package com.example.mysnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var listViewSnaps : ListView? = null
    var emails : ArrayList<String> = ArrayList()
    var snapsArrayList : ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        setTitle("Snaps")

        listViewSnaps = findViewById(R.id.listViewSnaps)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        listViewSnaps?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users")
                .child(auth.currentUser?.uid!!).child("snaps")
                .addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        emails.add(snapshot.child("from").value as String)
                        snapsArrayList.add(snapshot!!)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        var index = 0
                        for (snap: DataSnapshot in snapsArrayList) {
                            if(snap.key == snapshot.key) {
                                snapsArrayList.removeAt(index)
                                emails.removeAt(index)
                                adapter.notifyDataSetChanged()
                            }
                            index++
                        }
                    }
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })

        listViewSnaps?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            var snapshot = snapsArrayList.get(position)

            var intent = Intent(this, ViewSnapsActivity::class.java)
            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("snapKey", snapshot.key)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snap_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.createSnap) {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)

        } else if (item?.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(this, SignupLoginActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}
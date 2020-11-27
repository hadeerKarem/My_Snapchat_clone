package com.example.mysnapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.loader.content.AsyncTaskLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ViewSnapsActivity : AppCompatActivity() {

    var textViewMessage : TextView? = null
    var imageViewSnap : ImageView? = null
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snaps)

        textViewMessage = findViewById(R.id.textViewMessage)
        imageViewSnap = findViewById(R.id.imageViewSnap)

        textViewMessage?.text = intent.getStringExtra("message")

        val task = ImageDownloader()
        val myImage : Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageUrl")).get()
            imageViewSnap?.setImageBitmap(myImage)

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpsURLConnection
                connection.connect()
                val `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)

            } catch (e : Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //remove the snap from database
        FirebaseDatabase.getInstance().reference.child("users")
            .child(auth.currentUser?.uid!!).child("snaps")
            .child(intent.getStringExtra("snapKey")!!).removeValue()

        //remove the snap from firebase storage
        FirebaseStorage.getInstance().reference.child("images")
            .child(intent.getStringExtra("imageName")!!).delete()
    }
}
package com.example.mysnapchat

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    var imageViewCreateSnap : ImageView? = null
    var editTextMessage : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"
    var imageUrl :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        imageViewCreateSnap = findViewById(R.id.imageViewMySnap)
        editTextMessage = findViewById(R.id.editTextMessage)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    fun nextClicked(view: View) {
        // Get the data from an ImageView as bytes
        imageViewCreateSnap?.isDrawingCacheEnabled = true
        imageViewCreateSnap?.buildDrawingCache()

        val bitmap = (imageViewCreateSnap?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = FirebaseStorage.getInstance()
            .reference.child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Upload Failed!", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener(OnSuccessListener<Uri>(){
                imageUrl = it.toString()
                Log.i("URL", imageUrl!!)

                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("imageUrl", imageUrl!!)
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", editTextMessage?.text.toString())
                startActivity(intent)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage : Uri = data?.data!!

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                imageViewCreateSnap?.setImageBitmap(bitmap)
                Log.i("CreateSnapLog", "selected image is "+bitmap.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }
}
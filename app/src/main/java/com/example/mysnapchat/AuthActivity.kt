package com.example.mysnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    var editTextEmailAddress : EditText? = null
    var editTextPassword : EditText? = null
    private lateinit var auth : FirebaseAuth
    lateinit var extraValue : String
    val TAG = "AuthActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        editTextEmailAddress = findViewById(R.id.editTextEmailAddress)
        editTextPassword = findViewById(R.id.editTextPassword)
        val buttonAuth = findViewById<Button>(R.id.buttonAuth)

        auth = Firebase.auth

        //check if there is a user who already signed in
        if (auth.currentUser != null) {
            //so, go to the next activity
            updateUI()
        }

        //get the Intent Extra to know the button type
        val intent : Intent = intent
        extraValue = intent.getStringExtra("button").toString()

        if (extraValue.equals("login")) {
            setTitle("Login")
            buttonAuth.setBackgroundColor(resources.getColor(R.color.amaranth))
            buttonAuth.setText("Log In")
        }
        else if (extraValue.equals("signUp")) {
            setTitle("SignUp")
            buttonAuth.setBackgroundColor(resources.getColor(R.color.robinEggBlue))
            buttonAuth.setText("Sign Up")
        }
    }

    fun buttonClicked(view: View) {
        //check if we can log in the user
        if (extraValue.equals("login")) {
            auth.signInWithEmailAndPassword(editTextEmailAddress?.text.toString(), editTextPassword?.text.toString())
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        //SignIn Success, updateUI with the signed-in user's info
                        Log.i(TAG, "signInWithEmail: success")
                        updateUI()
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Login Failed.\nPlease Sign Up.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, SignupLoginActivity::class.java)
                        startActivity(intent)
                    }
                }
        } else if (extraValue.equals("signUp")){
            auth.createUserWithEmailAndPassword(editTextEmailAddress?.text.toString(), editTextPassword?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")

                        // Add to Database
                        val database = Firebase.database
                        val myRef = database.reference
                                .child("users")
                                .child(task.result?.user?.uid!!)
                                .child("email").setValue(editTextEmailAddress?.text.toString())

                        updateUI()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed. Try Again.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun updateUI() {
        Log.i(TAG, "current user is" + auth.currentUser?.email)
        //Move to the next activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}
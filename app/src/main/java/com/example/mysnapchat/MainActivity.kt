package com.example.mysnapchat

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivityLog"
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set Activity Title
        setTitle("Snapchat")

        //set Countdown timer to start Login/SignUp Activity
        val timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "Tick-Tock")
            }

            override fun onFinish() {
                Log.i(TAG, "Timer Finished")
                if (auth.currentUser != null) {
                    val loggedInIntent = Intent(this@MainActivity, SnapsActivity::class.java)
                    startActivity(loggedInIntent)
                } else {
                    val intent = Intent(this@MainActivity, SignupLoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        timer.start()
    }
}
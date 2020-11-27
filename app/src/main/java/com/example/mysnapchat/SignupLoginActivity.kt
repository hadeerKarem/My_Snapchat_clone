 package com.example.mysnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlin.system.exitProcess

 class SignupLoginActivity : AppCompatActivity() {

    var value : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_login)

        //set Activity Title
        setTitle("SignUp / Login")
    }

    fun loginClicked(view : View) {
        value = "login"
        startAuthActivity(value)
    }

    fun signUpClicked(view : View) {
        value = "signUp"
        startAuthActivity(value)
    }

    fun startAuthActivity(value: String) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("button", value)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
        exitProcess(0)
    }
}
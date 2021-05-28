package com.fortrade.tiktok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fortrade.tiktok.authentication.AuthActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser==null){
            startActivity(Intent(this,AuthActivity::class.java))
            finish()
        }else{
            Toast.makeText(this,"Already Sigh In",Toast.LENGTH_SHORT).show()
        }
    }
}
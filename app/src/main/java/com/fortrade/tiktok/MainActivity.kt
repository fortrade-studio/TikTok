package com.fortrade.tiktok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fortrade.tiktok.authentication.AuthFragment
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
           val authFragment = AuthFragment()
            val fragmentManager  = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment,authFragment)
            transaction.addToBackStack(null)
            transaction.commit()


        }else{
            Toast.makeText(this,"Already Sigh In",Toast.LENGTH_SHORT).show()
        }
    }
}
package com.fortrade.tiktok.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fortrade.tiktok.MainActivity
import com.fortrade.tiktok.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth:FirebaseAuth
    private lateinit var CountryCode:EditText
    private lateinit var GetOtp:Button
    private lateinit var PhoneNumber:EditText
    private lateinit var storedVerificationId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)
        CountryCode=findViewById(R.id.conCode)
        PhoneNumber=findViewById(R.id.phone_Box)
        GetOtp=findViewById(R.id.otp_Btn)
        auth= FirebaseAuth.getInstance()

        GetOtp.setOnClickListener {
            login()
        }
        callbacks=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val intent=Intent(this@PhoneAuthActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@PhoneAuthActivity,"Success",Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@PhoneAuthActivity, "{${p0.message}}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                storedVerificationId=p0
                val otpIntent = Intent(applicationContext,OTPActivity::class.java)
                otpIntent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(otpIntent)
                finish()
            }
        }

    }

    private fun login() {
        val conCode =CountryCode.text.toString()
        val phone = PhoneNumber.text.toString()

        val ph = "$conCode$phone"

        if (conCode.isNotEmpty()||phone.isNotEmpty()){
            val options=PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(ph)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}
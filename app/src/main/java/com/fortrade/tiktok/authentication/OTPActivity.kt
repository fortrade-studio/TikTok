package com.fortrade.tiktok.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fortrade.tiktok.MainActivity
import com.fortrade.tiktok.R
import com.google.firebase.auth.*

class OTPActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var OtpEnter:EditText
    private lateinit var verifyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        auth= FirebaseAuth.getInstance()
        OtpEnter = findViewById(R.id.otp_Edt)
        verifyButton = findViewById(R.id.verify_Btn)
        val storedVerificationId=intent.getStringExtra("storedVerificationId")

        verifyButton.setOnClickListener {
            val otp=OtpEnter.text.toString()
            if (otp.isNotEmpty()){
                val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(),otp)
                singInWithAuth(credential)
            }else{
                Toast.makeText(this@OTPActivity,"Enter OTP",Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun singInWithAuth(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this){
                    if (it.isSuccessful){
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        if (it.exception is FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(this@OTPActivity,"Invalid OTP",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }
}
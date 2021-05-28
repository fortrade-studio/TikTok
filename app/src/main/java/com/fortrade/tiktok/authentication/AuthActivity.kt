package com.fortrade.tiktok.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fortrade.tiktok.databinding.ActivityAuthBinding
import com.fortrade.tiktok.profile.UpdateProfileActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    //if Code Sending Failed, will be use to resend
    private var forceResendingToken:PhoneAuthProvider.ForceResendingToken?=null

    private var mCollBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
    private var mVerificationId:String?=null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "TAGES"
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scrollViewPhoneAuth.visibility = View.VISIBLE
        binding.ScrollViewOTP.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)


        mCollBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(this@AuthActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("phones","${e.message}")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSend : $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                //hide phone Auth scrollView
                binding.scrollViewPhoneAuth.visibility = View.GONE
                binding.ScrollViewOTP.visibility = View.VISIBLE

                Toast.makeText(this@AuthActivity, "Verification Code Send", Toast.LENGTH_SHORT).show()
                binding.textView5.text ="+91${binding.phoneBox.text}"
            }
        }
        //Phone Get Otp
        binding.otpBtn.setOnClickListener {
            val conCode = binding.conCode.text.toString()
            val phone = binding.phoneBox.text.toString()
            val fullNumber = "$conCode$phone"
            Log.d("fullNumber","full number: $fullNumber")
            Log.d("phone","number: $phone")
            //validate phone number
            if (TextUtils.isEmpty(fullNumber)){
                Toast.makeText(this@AuthActivity, "Please Enter Phone Number ", Toast.LENGTH_SHORT).show()

            }else{
                startPhoneNumberVerification(fullNumber)
            }
        }

        //Resend Code
        binding.resend.setOnClickListener {
            val conCode = binding.conCode.text.toString()
            val phone = binding.phoneBox.text.toString()
            val fullNumber = "$conCode$phone"
            Log.d("fullNumber","full number: $fullNumber")
            Log.d("phone","full number: $phone")
            //validate phone number
            if (TextUtils.isEmpty(fullNumber)){
                Toast.makeText(this@AuthActivity, "Please Enter Phone Number ", Toast.LENGTH_SHORT).show()

            }else{
                resendVerificationCode(fullNumber,forceResendingToken)
            }
        }

        //Verify
        binding.verifyBtn.setOnClickListener {
            val code = binding.otpEdt.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@AuthActivity, "Please Enter OTP", Toast.LENGTH_SHORT).show()

            }else{
                verifyPhoneNumberWithCode(mVerificationId,code)
            }
        }
    }

    //functions

    private fun startPhoneNumberVerification(fullNumber:String){
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()


        val options= PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fullNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCollBacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(fullNumber: String,token: PhoneAuthProvider.ForceResendingToken?){
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()


        val options= PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fullNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCollBacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId:String?,code:String){
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In...")

        firebaseAuth .signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser.phoneNumber
                Toast.makeText(this@AuthActivity, "Loggin with as $phone", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this,UpdateProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this@AuthActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
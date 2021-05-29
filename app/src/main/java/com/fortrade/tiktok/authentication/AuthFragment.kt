package com.fortrade.tiktok.authentication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.fortrade.tiktok.R
import com.fortrade.tiktok.databinding.FragmentAuthBinding
import com.fortrade.tiktok.profile.UpdateProfileFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken?=null

    private var mCollBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
    private var mVerificationId:String?=null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "TAGES"
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollViewPhoneAuth.visibility = View.VISIBLE
        binding.ScrollViewOTP.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCollBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
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

                Toast.makeText(context, "Verification Code Send", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Please Enter Phone Number ", Toast.LENGTH_SHORT).show()

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
                Toast.makeText(context, "Please Enter Phone Number ", Toast.LENGTH_SHORT).show()

            }else{
                resendVerificationCode(fullNumber,forceResendingToken)
            }
        }

        //Verify
        binding.verifyBtn.setOnClickListener {
            val code = binding.otpEdt.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_SHORT).show()

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
        .setActivity(context as Activity)
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
        .setActivity(context as Activity)
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
            Toast.makeText(context, "Loggin with as $phone", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_authFragment_to_updateProfileFragment)

        }
        .addOnFailureListener{e->
            progressDialog.dismiss()
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }
}


     override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
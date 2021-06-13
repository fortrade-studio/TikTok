package com.fortrade.tiktok.authentication

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.fortrade.tiktok.R
import com.fortrade.tiktok.databinding.FragmentAuthBinding
import com.fortrade.tiktok.profile.UpdateProfileFragmentDirections
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    var disable: Boolean = false
    lateinit var conCode : String
    lateinit var phone: String
    lateinit var phoneCurrentUser: String
    lateinit var resend: TextView
    lateinit var fullNumber: String
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCollBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var timer: MyCounter

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

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser!=null){
            Navigation.findNavController(requireView()).navigate(R.id.action_authFragment_to_homeFragment)
        }

        binding.scrollViewPhoneAuth.visibility = View.VISIBLE
        binding.ScrollViewOTP.visibility = View.GONE
        timer = MyCounter(60000, 1000)

        resend = view.findViewById(R.id.resend)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCollBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                binding.phoneBox.error = "Enter Valid Mobile Number"
                binding.phoneBox.requestFocus()
                Log.d("phoness", "${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSend : $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                //hide phone Auth scrollView
                binding.scrollViewPhoneAuth.visibility = View.GONE
                binding.ScrollViewOTP.visibility = View.VISIBLE

                Toast.makeText(context, R.string.CodeSend, Toast.LENGTH_SHORT).show()
                phoneCurrentUser = binding.phoneBox.text.toString()
                binding.textView5.text = "+91${binding.phoneBox.text}"
                timer.start()
            }
        }

                //Phone Get Otp
                binding.otpBtn.setOnClickListener {
                   conCode = binding.conCode.text.toString()
                   phone = binding.phoneBox.text.toString()
                   fullNumber = "$conCode$phone"
                    Log.d("fullNumber", "full number: $fullNumber")
                    Log.d("phone", "number: $phone")
                    //validate phone number
                    if (TextUtils.isEmpty(fullNumber)) {
                        Toast.makeText(context, R.string.PhoneNomber, Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        startPhoneNumberVerification(fullNumber)
                    }
                }

                //Resend Code
                binding.resend.setOnClickListener {

                    if (disable == true) {
                        Log.d("fullNumber", "full number: $fullNumber")
                        Log.d("phone", "full number: $phone")
                        //validate phone number

                       resendVerificationCode(fullNumber, forceResendingToken)
                        disable = false
                    }
                }


        //Verify
        binding.verifyBtn.setOnClickListener {
            val code = binding.otpView.otp.trim()
            if (TextUtils.isEmpty(code)){
                binding.otpView.showError()
                Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }else{
                verifyPhoneNumberWithCode(mVerificationId,code)
            }
        }
    }


//functions


            private fun startPhoneNumberVerification(fullNumber: String) {
                progressDialog.setMessage("Verifying Phone Number...")
                progressDialog.show()


                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(fullNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(mCollBacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }

            private fun resendVerificationCode(
                fullNumber: String,
                token: PhoneAuthProvider.ForceResendingToken?
            ) {
                progressDialog.setMessage("Resending Code...")
                progressDialog.show()


                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(fullNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(mCollBacks)
                    .setForceResendingToken(token)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }

            private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
                progressDialog.setMessage("Verifying Code...")
                progressDialog.show()

                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                signInWithPhoneAuthCredential(credential)

            }

            private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
                progressDialog.setMessage("Logging In...")


                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        val phone = firebaseAuth.currentUser.phoneNumber
                        Toast.makeText(context, "Loggin with as $phone", Toast.LENGTH_SHORT).show()
                        timer.cancel()

                        var ref = FirebaseDatabase.getInstance().getReference("userProfileData").child(phoneCurrentUser)
                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(context, "$phone exists", Toast.LENGTH_SHORT).show()

                                    val action = AuthFragmentDirections.actionAuthFragmentToUserProfileFragment(phoneCurrentUser)
                                    findNavController().navigate(action)
                                } else {
                                    Toast.makeText(context, "$phone not exists", Toast.LENGTH_SHORT).show()
                                    val action = AuthFragmentDirections.actionAuthFragmentToUpdateProfileFragment(phoneCurrentUser)
                                    findNavController().navigate(action)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show()
                            }
                        })


                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        binding.otpView.showError()
                        binding.otpView.requestFocus()
                    }
            }

            inner class MyCounter(millisInFuture: Long, countDownInterval: Long) :
                CountDownTimer(millisInFuture, countDownInterval) {

                override fun onFinish() {
                    println("Timer Completed.")
                    disable = true
                    resend.text = "Resend"
                }

                override fun onTick(millisUntilFinished: Long) {

                    resend.text = (millisUntilFinished / 1000).toString() + ""
                    println("Timer  : " + millisUntilFinished / 1000)
                }
            }


            override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
            }
        }

package com.fortrade.tiktok.profile

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fortrade.tiktok.MainActivity
import com.fortrade.tiktok.databinding.FragmentUpdateProfileBinding
import com.fortrade.tiktok.model.UserProfileData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UpdateProfileFragment : Fragment() {
    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var BirthDay: String
    var valid = true
    private lateinit var progressDialog: ProgressDialog
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.date!!.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val day = cal[Calendar.DAY_OF_MONTH]
            val dialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year, month, day
                )
            }
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month = month + 1
            Log.d(TAG, "onDateSet: mm/dd/yyy: $month/$day/$year")
            BirthDay = "$month/$day/$year"
            binding.date!!.text = BirthDay
        }

        //Pick images
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 82)

        }

        binding.saveProfile.setOnClickListener {

            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedProfileUri == null) {
            progressDialog.dismiss()
            Toast.makeText(
                context,
                "Please Select Image",
                Toast.LENGTH_LONG
            ).show()

            return
        }
        progressDialog.show()
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedProfileUri!!)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Successfully upload",
                    Toast.LENGTH_SHORT
                ).show()
                //Get Download Url
                ref.downloadUrl.addOnSuccessListener {
                    //Save Data into Firebase Database
                    saveUserProfileData(it.toString())
                }

            }
    }

    private fun saveUserProfileData(ProfileImageUrl: String) {

        val radio: RadioButton? = view?.findViewById(binding.gender.checkedRadioButtonId)

        val fullName = binding.name.editableText.toString()
        val username = binding.userName.editableText.toString()
        val emailId = binding.emailBox.editableText.toString()
        val phoneNumber = binding.phone.editableText.toString()
        val Bio = binding.bio.editableText.toString()
        val website = binding.website.editableText.toString()
        val gender ="${radio?.text}"

        checkField(binding.name)
        checkField(binding.userName)
        checkField(binding.emailBox)
        checkField(binding.phone)
        checkField(binding.bio)

        if (valid){
            val ref = FirebaseDatabase.getInstance().getReference("userProfileData").push()

            val user = UserProfileData(fullName, username, emailId, phoneNumber, Bio, website, gender,BirthDay,ProfileImageUrl)

            ref.setValue(user)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "UploadData", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context, MainActivity::class.java))

                }
        }else{

            progressDialog.dismiss()
            Toast.makeText(context, "please enter valid data", Toast.LENGTH_LONG).show()
        }
    }

    var selectedProfileUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 82 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedProfileUri = data.data

//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedProfileUri)
            binding.profileImage.setImageURI(selectedProfileUri)
        }
    }

    fun checkField(textField: EditText): Boolean {
        if (textField.text.toString().isEmpty()) {
            textField.error = "Error"
            valid = false
        } else {
            valid = true
        }
        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


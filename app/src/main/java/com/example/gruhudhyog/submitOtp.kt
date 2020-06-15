package com.example.gruhudhyog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.gruhudhyog.submitOtp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.*
import kotlinx.android.synthetic.main.activity_submit_otp.*
import java.util.concurrent.TimeUnit

class submitOtp : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var phoneNumber: String
    lateinit var mCallbacks: OnVerificationStateChangedCallbacks
    var storedVerificationId: String? = null
    lateinit var resendToken: ForceResendingToken

    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdi : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_otp)
        mAuth=FirebaseAuth.getInstance()

        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)
        sharedPrefEdi = sharedPref.edit()

        var myIntent = getIntent()
        phoneNumber = myIntent.getStringExtra("pn")
        resendOtp.visibility = View.INVISIBLE
        submitOtp.setOnClickListener{
            val otp = editOtp.text.toString()
//            var storedVerificationId = myIntent.getStringExtra("storedVerificationId")
//            var resendToken = myIntent.getStringExtra("resendToken")

            val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
            signInWithPhoneAuthCredential(credential)
        }
        resendOtp.setOnClickListener{
            resendOtp.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            verify()
        }
        resendOtp.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        verify()
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful){
                // Sign in success, update UI with the signed-in user's information

                sharedPrefEdi.putString("loginNum", phoneNumber)
                sharedPrefEdi.commit()

                Toast.makeText(this, "Login Success !", Toast.LENGTH_SHORT).show()
//                val user = task.result?.user
                // ...
                startActivity(Intent(this, Dashboard::class.java))
                //============================================================================================================================================+ACTIVATE THIS !!!!!
            } else {
                // Sign in failed, display a message and update the UI
                Log.w("TAG", "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Phone Number or Password is wrong !", Toast.LENGTH_SHORT).show()
                    // The verification code entered was invalid
                }else{
                    Toast.makeText(this, "Login Failed !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun verify(){
//        val phoneNumber = editPhoneNum.text.toString ()
        verificationCallbacks()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )
    }
    private fun verificationCallbacks() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
//                TODO("Not yet implemented")
                progressBar.visibility = View.INVISIBLE
//                Toast.makeText(getApplicationContext(), "Verification COMPLETED !!!", Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
//                TODO("Not yet implemented")
                Toast.makeText(getApplicationContext(), "Failed to send OTP !", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Err", p0.toString())

                //===========================================================================================================
                val builder = AlertDialog.Builder(this@submitOtp)
                //set title for alert dialog
                builder.setTitle("Error sending OTP !")
                //set message for alert dialog
                builder.setMessage(p0.toString())
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                //performing positive action
                builder.setPositiveButton("Yes") { dialogInterface, which ->
//                        Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
                ///===========================================================================================================
                progressBar.visibility = View.INVISIBLE
            }

            //            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
//                super.onCodeSent(p0, p1)
//            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")
//                super.onCodeSent(verificationId, token)
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
//                Toast.makeText(this@MainActivity, verificationId, Toast.LENGTH_SHORT).show()
                resendToken = token
//                var Inte = Intent(this, SubmitOtp::class.java)
//                Inte.putExtra("storedVerificationId", storedVerificationId)
//                Inte.putExtra("resendToken", resendToken)
//                startActivity(Inte)
                // ...
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                resendOtp.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}
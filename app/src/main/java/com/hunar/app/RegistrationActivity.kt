package com.hunar.app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        var db = FirebaseFirestore.getInstance()

        var sharedPref = getSharedPreferences("com.hunar.app", MODE_PRIVATE)
        var sharedEdit = sharedPref.edit()



        regSubmit.setOnClickListener{
            var uName = regNameETxt.text.toString()
            var uAddr = regAddrETxt.text.toString()
            db.collection("users").document(FirebaseAuth.getInstance().uid.toString()).set(
                mapOf("userName" to uName, "userAddr" to uAddr), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("TAG", "Adding user in db is successful")
                    sharedEdit.putString("userName", uName)
                    sharedEdit.putString("userAddr", uAddr)
                    sharedEdit.apply()
                }
                .addOnFailureListener { Log.e("TAG", "Adding user in db failed !$it") }

            startActivity(Intent(this@RegistrationActivity, Dashboard::class.java))
            finish()
        }
        regSkip.setOnClickListener{
            startActivity(Intent(this@RegistrationActivity, Dashboard::class.java))
        }
    }
}
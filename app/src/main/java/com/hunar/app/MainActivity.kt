package com.hunar.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
        var phoneNum = sharedPref.getString("loginNum", null)

        if(phoneNum==null){
            Log.d("TAG","ABCD")
            startActivity(Intent(this@MainActivity, LanguageSelect::class.java))
        }else{
            Log.d("TAG","ABC")
            startActivity(Intent(this@MainActivity, Dashboard::class.java))
//            startActivity(Intent(this@MainActivity, SubmitOrder::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        finishAffinity()
    }
}
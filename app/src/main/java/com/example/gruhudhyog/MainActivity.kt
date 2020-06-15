package com.example.gruhudhyog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yariksoffice.lingver.Lingver
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)
        var phoneNum = sharedPref.getString("phoneNum", null)

        if(phoneNum==null){
            startActivity(Intent(this@MainActivity, LanguageSelect::class.java))
        }else{
            startActivity(Intent(this@MainActivity, Dashboard::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        finishAffinity()
    }
}
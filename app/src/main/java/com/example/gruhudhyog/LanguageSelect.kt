package com.example.gruhudhyog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.activity_language_select.*
import java.util.*

class LanguageSelect : AppCompatActivity() {
    lateinit var sharedPref : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_select)


        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)

        Toast.makeText(this, sharedPref.getString("loginNum", null).toString(), Toast.LENGTH_SHORT).show()


        guBtn.setOnClickListener {
            setAppLocale("gu")
        }
        hiBtn.setOnClickListener {
            setAppLocale("hi")
        }
        enBtn.setOnClickListener {
            setAppLocale("en")
        }
        regText.setOnClickListener{
            startActivity(Intent(this@LanguageSelect, getPhnum::class.java))
        }
        skip.setOnClickListener{
            startActivity(Intent(this@LanguageSelect, Dashboard::class.java))
        }
    }
    fun setAppLocale(p0: String){
        var dm = resources.displayMetrics
        var conf = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(p0))
        }else{
            conf.locale = Locale(p0)
        }
        resources.updateConfiguration(conf, dm)
        recreate();
    }

//    override fun onResume() {
//        super.onResume()
//        finishAffinity()
//    }
}




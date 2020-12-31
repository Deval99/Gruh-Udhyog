package com.hunar.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_get_phnum.*

class GetPhnum : AppCompatActivity() {
    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_phnum)

        sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPref.edit()

        submit.setOnClickListener {
            var phoneNumber = "+"+countryC.selectedCountryCode+editPhoneNum.text.toString()
//            Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show()


            var inte = Intent(this@GetPhnum, submitOtp::class.java)
            inte.putExtra("pn",phoneNumber)
            startActivity(inte)
        }
        skip.setOnClickListener{
            startActivity(Intent(this@GetPhnum, Dashboard::class.java))
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
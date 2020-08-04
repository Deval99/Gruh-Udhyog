package com.hunar.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_get_phnum.*

class getPhnum : AppCompatActivity() {
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


            var inte = Intent(this@getPhnum, submitOtp::class.java)
            inte.putExtra("pn",phoneNumber)
            startActivity(inte)
        }
        skip.setOnClickListener{
            startActivity(Intent(this@getPhnum, Dashboard::class.java))
        }

        loginFake.setOnClickListener{
            sharedPrefEdit.putString("loginNum", "+919512492862")
            sharedPrefEdit.commit()

            var inte = Intent(this@getPhnum, Dashboard::class.java)
            inte.putExtra("pn","+919512492862")
            startActivity(inte)
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, this.getString(R.string.restart_app), Toast.LENGTH_SHORT).show()
    }
}
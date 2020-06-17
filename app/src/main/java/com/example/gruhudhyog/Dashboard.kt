package com.example.gruhudhyog

//import android.widget.Toolbar
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_search.*
import java.io.File
import java.util.*


class Dashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        //FOR DISABLING STATUSBAR (SHUTTER)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dashboard)

        searchBtnDb.setOnClickListener{
            startActivity(Intent(this@Dashboard, SearchActivity::class.java))
        }
        micBtnDb.setOnClickListener{
            startActivity(Intent(this@Dashboard, SearchActivity::class.java))
        }
        editSearchDb.setOnClickListener{
            startActivity(Intent(this@Dashboard, SearchActivity::class.java))
        }

        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPref.edit()

        Toast.makeText(this, sharedPref.getString("loginNum", null).toString(), Toast.LENGTH_SHORT).show()


        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        //TEMP


        //FOR LEFT NAVIGATION MENU OPEN
        optLeftOpen.setOnClickListener {
            Toast.makeText(this, "Haa !!!", Toast.LENGTH_SHORT).show()
            drawer.openDrawer(GravityCompat.START)
        }

        userProfIco.setOnClickListener{
            var inte = Intent(this, EditProfile::class.java)
            startActivity(inte)
        }



        //================================================================================================================
        //================================================================================================================
        //==========================================NavigationBar=========================================================
        //================================================================================================================

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        var navView = findViewById<NavigationView>(R.id.nav_view)
        nav_view.getMenu().getItem(0).setActionView(R.layout.menu_img_left);

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@Dashboard)

        //=================================================================================================================================
        //============================================On Click Listeners NavDrawer=========================================================
        //=================================================================================================================================
        nv_logout.setOnClickListener{
            drawer.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Logout pending", Toast.LENGTH_SHORT).show()
        }
        nv_abtus.setOnClickListener{
            drawer.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "About us pending", Toast.LENGTH_SHORT).show()
        }
        nv_hlpfb.setOnClickListener{
            drawer.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Help and feedback pending", Toast.LENGTH_SHORT).show()
        }
        nv_prpo.setOnClickListener{
            drawer.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Privacy Policy pending", Toast.LENGTH_SHORT).show()
        }

        nv_logout.setOnClickListener{
            sharedPrefEdit.remove("loginNum")
            sharedPrefEdit.commit()
            startActivity(Intent(this, LanguageSelect::class.java))
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mgmtPayment -> {
                Toast.makeText(this, "Payment Management", Toast.LENGTH_SHORT).show()
            }
            R.id.GotoSett -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@Dashboard, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Dashboard, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }


}
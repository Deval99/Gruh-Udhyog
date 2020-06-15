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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
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

        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPref.edit()

        Toast.makeText(this, sharedPref.getString("loginNum", null).toString(), Toast.LENGTH_SHORT).show()


        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        //FOR LEFT NAVIGATION MENU OPEN
        optLeftOpen.setOnClickListener {
            Toast.makeText(this, "Haa !!!", Toast.LENGTH_SHORT).show()
            drawer.openDrawer(GravityCompat.START)
        }

        userProfIco.setOnClickListener{
            var inte = Intent(this, EditProfile::class.java)
            startActivity(inte)
        }

        //FOR SEARCH MIC
        //===============================================================================================================================================
        //===============================================================================================================================================
        nav_view.getMenu().getItem(0).setActionView(R.layout.menu_img_left);
        if (ContextCompat.checkSelfPermission(this@Dashboard, RECORD_AUDIO) == PERMISSION_GRANTED) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPerm("Permission", getString(R.string.askMicP))
            } else {
                val builder = AlertDialog.Builder(this@Dashboard)
                builder.setTitle("Permission")
                builder.setMessage(getString(R.string.askMic))
                builder.setPositiveButton("Ok") { dialogInterface, which ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }

        val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        
        //FOR DIFFERENT LANGUAGES
        if (resources.configuration.locale.toString().equals("gu")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "gu-IN");
        } else if (resources.configuration.locale.toString().equals("hi")){
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        } else {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        }
        //=====

        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                //getting all the matches
                val matches = bundle
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                //displaying the first match
                if (matches != null) editSearch.setText(matches[0])
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        editSearch.setOnClickListener{
            editSearch.hint = getString(R.string.searchTxt)
        }
        micBtn.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    mSpeechRecognizer.stopListening()
                    editSearch.hint = getString(R.string.holdSpk)
                    Toast.makeText(this@Dashboard, "NOW SEARCH !", Toast.LENGTH_SHORT).show()
                }
                MotionEvent.ACTION_DOWN -> {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                    editSearch.setText("")
                    editSearch.hint = getString(R.string.listening)
                }
                MotionEvent.ACTION_BUTTON_RELEASE -> {
                    editSearch.hint = getString(R.string.searchTxt)
                }
            }
            false
        }
        //===============================================================================================================================================
        //===============================================================================================================================================

        //================================================================================================================
        //================================================================================================================
        //==========================================NavigationBar=========================================================
        //================================================================================================================

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        var navView = findViewById<NavigationView>(R.id.nav_view)

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

        logout.setOnClickListener{
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPerm(p0:String, p1:String){
        val builder = AlertDialog.Builder(this@Dashboard)
        builder.setTitle(p0)
        builder.setMessage(p1)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            requestPermissions(arrayOf(RECORD_AUDIO), 101)
        }
        builder.setNegativeButton("No"){ dialogInterface, which: Int ->
            Toast.makeText(this@Dashboard, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
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
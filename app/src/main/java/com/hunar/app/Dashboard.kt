package com.hunar.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.db_search_box_layout.*
import java.util.*

class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, KeyboardHeightObserver{
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor
    lateinit var keyboardHeightProvider : KeyboardHeightProvider
    lateinit var db_search : View
    var prodList = ArrayList<Product>()
    var idList = ArrayList<String>()
    lateinit var dbRef : DatabaseReference

    private val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.
    private var mBackPressed: Long = 0

//    lateinit var prodAdapter : ProductAdapter

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.e("High", count.toString())
        if (count == 0) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){
                val view = findViewById<View>(R.id.drawer_layout)
                view.post { keyboardHeightProvider.close() }
                super.onBackPressed();
                return;
            }
            else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

            mBackPressed = System.currentTimeMillis();


        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val orientationLabel =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) "portrait" else "landscape"

        Log.i(
            "Dashboard",
            toggleSearch(height, orientationLabel)
        )
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.db_bot_nav_home -> {

                supportFragmentManager.beginTransaction().replace(
                    R.id.db_frag, HomeFrag(
                        supportFragmentManager,
                        this
                    ), "HomeFrag"
                ).commit()
            }
            R.id.db_bot_nav_search -> {
                onSearchClicked()
            }
            R.id.db_bot_nav_orders -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.db_frag,
                    OrdersFrag(),
                    "OrdersFrag"
                )
                    .commit()
            }
            R.id.mgmtPayment -> {
                Toast.makeText(this, "Payment Management", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.GotoSett -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }

    private fun onSearchClicked(){
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun toggleSearch(height: Int, orientation: String):String{
        if(height>0) {

            if (supportFragmentManager.findFragmentByTag("OverlapFrag") == null) {

                var overlapFrag = OverlapFrag(
                    supportFragmentManager,
                    this
                )
                var frag = supportFragmentManager.beginTransaction().add(
                    R.id.db_frag, overlapFrag, "OverlapFrag"
                )
                Log.e("TAG", supportFragmentManager.findFragmentByTag("OverlapFrag").toString())

                frag.addToBackStack("OverlapFrag")
                frag.commit()
            }
        }else{
            if(supportFragmentManager.findFragmentByTag("OverlapFrag")!=null) {
                onBackPressed()
            }
            if(dashboardLayout.getViewById(R.id.db_search) != null){
                db_search.visibility = View.INVISIBLE
            }
        }
        return ""
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)


        mBackPressed = System.currentTimeMillis()

        dbRef = FirebaseDatabase.getInstance().reference.child("Product")

        //CalculateKeyboardHeight
        keyboardHeightProvider = KeyboardHeightProvider(this)
        // make sure to start the keyboard height provider after the onResume
        // of this activity. This is because a popup window must be initialised
        // and attached to the activity root view.

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.post { keyboardHeightProvider.start() }


        //For login info retrieval
        sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPref.edit()


        //FOR LEFT NAVIGATION MENU OPEN
        //Shifted to HomeFrag


        //FOR FRAGMENTS/ SET DEFAULT
        var bottomNav : BottomNavigationView = findViewById(R.id.db_bot_nav_layout)
        supportFragmentManager.beginTransaction().replace(
            R.id.db_frag, HomeFrag(
                supportFragmentManager, this
            ), "HomeFrag"
        ).commit()
        bottomNav.setOnNavigationItemSelectedListener(this)



        //NAVIGATION BAR
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
            sharedPrefEdit.clear()
            sharedPrefEdit.commit()
            startActivity(Intent(this, LanguageSelect::class.java))
        }

        //FOR SEARCH MIC
        //===============================================================================================================================================
        //===============================================================================================================================================







        //===============================================================================================================================================
        //===============================================================================================================================================
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public fun askPerm(p0: String, p1: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(p0)
        builder.setMessage(p1)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
        builder.setNegativeButton("No"){ dialogInterface, which: Int ->
            Toast.makeText(this, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun dialogBox(p0: String, p1: String){
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.sad_vector)
        builder.setTitle(p0)
        builder.setMessage(p1)
//        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setNeutralButton("OK") { dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}


package com.hunar.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, KeyboardHeightObserver{
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor
    lateinit var keyboardHeightProvider : KeyboardHeightProvider
    lateinit var db_search : View
    lateinit var db_search_lt : View

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
                supportFragmentManager.beginTransaction().replace(R.id.db_frag, HomeFrag()).commit()
            }
            R.id.db_bot_nav_search -> {
                onSearchClicked()
            }
            R.id.db_bot_nav_orders -> {
                supportFragmentManager.beginTransaction().replace(R.id.db_frag, OrdersFrag()).commit()
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

        if(height>0){
            if(dashboardLayout.getViewById(R.id.db_search) == null) {
                db_search_lt =
                    LayoutInflater.from(this).inflate(R.layout.db_search_layout, dashboardLayout)
                db_search = findViewById<View>(R.id.db_search)


                val param = db_search.layoutParams as ViewGroup.MarginLayoutParams
                param.topMargin = drawer_layout.getRootView().getHeight() - height - 300
                param.height = 200
                db_search.layoutParams = param


                var searchBtn = findViewById<View>(R.id.searchBtn)
                searchBtn.setOnClickListener(){
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)

                    var int = Intent(this, SearchActivity::class.java)
                    startActivity(int)
                }
            }else{
                db_search.visibility = View.VISIBLE
            }
        }else{
            if(dashboardLayout.getViewById(R.id.db_search) != null){
                db_search.visibility = View.INVISIBLE
            }
        }
        return ""
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FOR DISABLING STATUSBAR (SHUTTER)
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard)


        gruh2.setOnClickListener{
            startActivity(Intent(this@Dashboard, SubmitOrder::class.java))
        }

        //CalculateKeyboardHeight
        keyboardHeightProvider = KeyboardHeightProvider(this)
        // make sure to start the keyboard height provider after the onResume
        // of this activity. This is because a popup window must be initialised
        // and attached to the activity root view.
        val view = findViewById<View>(R.id.drawer_layout)
        view.post { keyboardHeightProvider.start() }


        //For login info retrieval
        sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPref.edit()


        //FOR LEFT NAVIGATION MENU OPEN
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        optLeftOpen.setOnClickListener {
            Toast.makeText(this, "Left Drawer Opened", Toast.LENGTH_SHORT).show()
            drawer.openDrawer(GravityCompat.START)
        }

        //RIGHT SIDE DP CLICK
        userProfIco.setOnClickListener{
            var inte = Intent(this, EditProfile::class.java)
            startActivity(inte)
        }

        //FOR FRAGMENTS/ SET DEFAULT
        var bottomNav : BottomNavigationView = findViewById(R.id.db_bot_nav_layout)
        supportFragmentManager.beginTransaction().replace(R.id.db_frag, HomeFrag()).commit()
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
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            101 -> {
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    Toast.makeText(this@Dashboard, "Permission Granted", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@Dashboard, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
//                }
//                return
//            }
//        }
//    }
}
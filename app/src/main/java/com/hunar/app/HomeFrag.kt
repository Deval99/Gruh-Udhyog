package com.hunar.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Index
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.db_home_frag.*
import kotlinx.android.synthetic.main.db_home_frag.view.*
import kotlinx.android.synthetic.main.db_search_frag.*
import java.io.File
import java.util.ArrayList

class HomeFrag(val supportFragmentManager: FragmentManager?, val dashboard: Dashboard?) : Fragment(){

    constructor() : this(null, null){
    }

    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor
    var meals = ArrayList<SliderClass>()
    lateinit var abc : View
    var sellerList = ArrayList<Seller>()
    var idList = ArrayList<String>()
    lateinit var prodAdapter : SellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        abc = inflater.inflate(R.layout.db_home_frag, container, false)


        if(abc==null){
            Log.e("Error", "View Null")
            return abc
        }
        if(dashboard==null){
            return abc
        }
        var dbCategoryList = abc.findViewById<RecyclerView>(R.id.dbCategoryList)
        val list: ArrayList<Categories> = ArrayList()
        var db  = FirebaseFirestore.getInstance()
        db.collection("categories")
            .orderBy("categoryName")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        list.add(Categories(document["categoryName"].toString(), "DESC_PENDING", document["categoryImage"].toString()))

                        Log.d("TAG", document.id + " => " + document["categoryName"])
                    }
                    dbCategoryList.adapter = CategoryAdapter(supportFragmentManager, list, dashboard, "HF")
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        dbCategoryList.setHasFixedSize(true)
        //recView.setLayoutManager(new LinearLayoutManager(this));
        //recView.setLayoutManager(new LinearLayoutManager(this));
        val mLayoutManager = GridLayoutManager(dashboard, 2, RecyclerView.HORIZONTAL, false)
        dbCategoryList.layoutManager = mLayoutManager


        val gruhText = abc.findViewById<TextView>(R.id.hunarTitle)
        gruhText.setOnClickListener{
            startActivity(Intent(abc.context, SubmitOrder::class.java))
        }

        val optLeftOpenV = abc.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.optLeftOpen)
        var drawer = dashboard.findViewById<DrawerLayout>(R.id.drawer_layout)
        optLeftOpenV.setOnClickListener {
            Toast.makeText(abc.context, "Left Drawer Opened", Toast.LENGTH_SHORT).show()
            drawer.openDrawer(GravityCompat.START)
        }



        val userProfIcoV = abc.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.userProfIco)
        //RIGHT SIDE DP CLICK
        userProfIcoV.setOnClickListener{
            var inte = Intent(abc.context, EditProfile::class.java)
            startActivity(inte)
        }

        prodAdapter = SellerAdapter(supportFragmentManager, abc.context, sellerList, idList, abc.context.filesDir.path.toString())


        val viewPager : ViewPager = abc.findViewById(R.id.db_viewPager) as ViewPager

        sharedPref = activity?.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE) as SharedPreferences
        sharedPrefEdit = sharedPref.edit()

        var dbSeeAllV = abc.findViewById<Button>(R.id.dbSeeAll)
        dbSeeAllV.setOnClickListener{
//            startActivity(Intent(abc.context, FetchCategories::class.java))
            if(dashboard == null){
                return@setOnClickListener
            }

            val titleBarDb = dashboard.findViewById<LinearLayout>(R.id.titleBarDb)
            titleBarDb.visibility = View.INVISIBLE

            val frameLayout: FrameLayout = dashboard.findViewById<View>(R.id.db_frag) as FrameLayout
            val params: ConstraintLayout.LayoutParams = frameLayout.layoutParams as ConstraintLayout.LayoutParams

            params.setMargins(0, 0, 0, 0)
            frameLayout.layoutParams = params

            if(supportFragmentManager == null){
                return@setOnClickListener
            }

            val frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CategoriesFrag(supportFragmentManager, dashboard)
            )
            frag.addToBackStack("CategoriesFrag")
            frag.commit()
        }

        //Toast.makeText(activity?.applicationContext as Context, sharedPref.getString("loginNum", null).toString(), Toast.LENGTH_SHORT).show()

        var ref = FirebaseStorage.getInstance().getReference().child("db_slider").listAll()

        ref.addOnCompleteListener() {
            for (str in ref.result?.items!!.iterator()) {
                str.downloadUrl.addOnSuccessListener {
//                    meals.add(SliderClass(str.name, it.toString()))

                    meals.add(
                        SliderClass(
                            "",
                            it.toString()
                        )
                    )
                    if (activity != null) {
                        val vpha: ViewPagerAdapter =
                            ViewPagerAdapter(meals, activity?.applicationContext as Context)
                        viewPager.adapter = vpha
                        viewPager.setPadding(20, 0, 150, 0)
                        vpha.notifyDataSetChanged()
                    }
                }
            }
        }

        //TEMP
        val db_topSellingRecView = abc.findViewById<RecyclerView>(R.id.db_topSellingRecView)

        db_topSellingRecView.adapter = prodAdapter

//        var catBtnArt = abc.findViewById<Button>(R.id.catBtnArt)
//        catBtnArt.setOnClickListener(LoadCategoryFrag("Art"))
//        var catBtnBeauty = abc.findViewById<Button>(R.id.catBtnBeauty)
//        catBtnBeauty.setOnClickListener(LoadCategoryFrag("Beauty & Health"))
//        var catBtnCraft = abc.findViewById<Button>(R.id.catBtnCraft)
//        catBtnCraft.setOnClickListener(LoadCategoryFrag("Craft"))
//        var catBtnFood = abc.findViewById<Button>(R.id.catBtnFood)
//        catBtnFood.setOnClickListener(LoadCategoryFrag("Food & Snacks"))
//        var catBtnGrocery = abc.findViewById<Button>(R.id.catBtnGrocery)
//        catBtnGrocery.setOnClickListener(LoadCategoryFrag("Grocery"))
//        var catBtnHome = abc.findViewById<Button>(R.id.catBtnHome)
//        catBtnHome.setOnClickListener(LoadCategoryFrag("Home Decor"))
//        var catBtnKids = abc.findViewById<Button>(R.id.catBtnKids)
//        catBtnKids.setOnClickListener(LoadCategoryFrag("Kids Zone"))
//        var catBtnPickle = abc.findViewById<Button>(R.id.catBtnPickle)
//        catBtnPickle.setOnClickListener(LoadCategoryFrag("Pickles,Masala & Chutneys"))
//        var catBtnPuja = abc.findViewById<Button>(R.id.catBtnPuja)
//        catBtnPuja.setOnClickListener(LoadCategoryFrag("Puja & Spiritual"))
//        var catBtnSweet = abc.findViewById<Button>(R.id.catBtnSweet)
//        catBtnSweet.setOnClickListener(LoadCategoryFrag("Sweets"))


        //===================================================================================================================
        //===================================================================================================================
        val client = Client("OVGQESHJVC", "bae0c9e4a2369b45bb0ca74f41132849")
        val index: Index = client.getIndex("sellers")

        val completionHandler: CompletionHandler =
            CompletionHandler { content, error ->
                if(content==null){
                    Log.d("TAG", "No Rows Found")
                    return@CompletionHandler
                }

                var count = Integer.valueOf(content.get("nbHits").toString()) - 1

                if(count == -1){
                    searchActNoRecFound.visibility = View.VISIBLE
                }

                for (x in 0..count){
                    idList.add(content.getJSONArray("hits").getJSONObject(x).get("objectID").toString())
//                    Log.e("TAG", "Request Completed   "+ content.getJSONArray("hits").getJSONObject(x).get("objectID"))
                    var pi = content.getJSONArray("hits").getJSONObject(x);
                    Log.e("TAG", "Request Completed   "+ content.getJSONArray("hits").getJSONObject(x).get("sellerName"))
                    lateinit var sellerAddr : String
                    lateinit var sellerCat : String;
                    lateinit var sellerCity : String;
                    lateinit var sellerDesc : String;
                    lateinit var sellerName : String;
                    lateinit var sellerPhone : String;
                    lateinit var sellerState : String;

                    try {
                        sellerAddr = pi.get("sellerAddress").toString()
                    }catch (e:Exception){ sellerAddr = "NULL" }
                    try {
                        sellerCat = pi.get("sellerCategories").toString()
                    }catch (e:Exception){ sellerCat = "NULL" }
                    try {
                        sellerCity = pi.get("sellerCity").toString()
                    }catch (e:Exception){ sellerCity = "NULL" }
                    try {
                        sellerDesc = pi.get("sellerDescription").toString()
                    }catch (e:Exception){ sellerDesc = "NULL" }
                    try {
                        sellerName = pi.get("sellerName").toString()
                    }catch (e:Exception){ sellerName = "NULL" }
                    try {
                        sellerPhone = pi.get("sellerPhone").toString()
                    }catch (e:Exception){ sellerPhone = "NULL" }
                    try {
                        sellerState = pi.get("sellerState").toString()
                    }catch (e:Exception){ sellerState = "NULL" }

                    sellerList.add(Seller(content.getJSONArray("hits").getJSONObject(x).get("objectID").toString(), sellerAddr, sellerCat, sellerCity, sellerDesc, sellerName, sellerPhone, sellerState))
                }
                var ada = SellerAdapter(supportFragmentManager, abc.context, sellerList, idList, abc.context.filesDir.path)
                abc.db_topSellingRecView.adapter = ada
                abc.db_topSellingRecView.isNestedScrollingEnabled = false
                abc.db_topSellingRecView.layoutManager = LinearLayoutManager(abc.context)
            }

        index.searchAsync(com.algolia.search.saas.Query(""), completionHandler);
        //===================================================================================================================
        //===================================================================================================================


        return abc
    }

    //=====================================================================================================================
    //Fetch Image Function, No longer used

//    fun fetchImage(id: String){
//        var storRef = FirebaseStorage.getInstance().getReference("ProductImages")
//        val localFile: File = File(abc.context.filesDir.path+"/ProductImages/"+id)
////        val localFilePath: File = File(/ProductImages")
//
////        localFilePath.mkdirs()
////        val localFile = File(localFilePath, id+".jpg")
////        Toast.makeText(this@MainActivity, localFile.path.toString(), Toast.LENGTH_SHORT).show()
//        Log.d("ABC", localFile.absolutePath)
//        storRef.child(id+".jpg").getFile(localFile)
//            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
////                Toast.makeText(this@MainActivity, "Successfully fetched images", Toast.LENGTH_SHORT).show()
//                // Successfully downloaded data to local file
////                var newFile = File(filesDir.path+"/ProductImages/"+id)
////                if(newFile.exists()){
////                    Log.d("ABC", "Exists")
////                }else{
////                    Log.d("ABC", "Not Exists")
////                }
////                var myBitmap = BitmapFactory.decodeFile(newFile.absolutePath)
////                if (myBitmap==null){
////                    Log.d("ABC", "Error aavi2"+filesDir+"/ProductImages/"+id)
////                }
////                micBtn.setImageBitmap(myBitmap)
//
//                // ...
//            }).addOnFailureListener(OnFailureListener {
//                // Handle failed download
//                Toast.makeText(abc.context, "Failed to store image file !"+it.message, Toast.LENGTH_SHORT).show()
//                // ...
//            })
//    }
    //=====================================================================================================================

    inner class LoadCategoryFrag(val str : String) : View.OnClickListener{
        override fun onClick(v: View?) {

            if(dashboard == null){
                return
            }
            val titleBarDb = dashboard.findViewById<LinearLayout>(R.id.titleBarDb)
            titleBarDb.visibility = View.INVISIBLE


            val frameLayout: FrameLayout =
                dashboard.findViewById<View>(R.id.db_frag) as FrameLayout

            val params: ConstraintLayout.LayoutParams =
                frameLayout.getLayoutParams() as ConstraintLayout.LayoutParams
            params.setMargins(0, 0, 0, 0)
            frameLayout.setLayoutParams(params)

            if(supportFragmentManager == null){
                return
            }
           var frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CategoryFrag(
                    supportFragmentManager, str, null
                )
            )
            frag.addToBackStack("CategoryFrag")
            frag.commit()
        }
    }
}


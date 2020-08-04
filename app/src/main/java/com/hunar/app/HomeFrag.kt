package com.hunar.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.ArrayList

class HomeFrag : Fragment(){


    lateinit var sharedPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor
    var meals = ArrayList<SliderClass>()
    lateinit var abc : View

    var prodList = ArrayList<Product>()
    var idList = ArrayList<String>()
    lateinit var prodAdapter : TopSellingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var abc : View = inflater.inflate(R.layout.db_home_frag, container, false)
        if(abc==null){
            Log.e("Error", "View Null")
            return inflater.inflate(R.layout.db_home_frag, container, false)
        }

        prodAdapter = TopSellingAdapter(abc.context, prodList, idList, abc.context.filesDir.path.toString())

        val viewPager : ViewPager = abc.findViewById(R.id.db_viewPager) as ViewPager

        sharedPref = activity?.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE) as SharedPreferences
        sharedPrefEdit = sharedPref.edit()

        Toast.makeText(activity?.applicationContext as Context, sharedPref.getString("loginNum", null).toString(), Toast.LENGTH_SHORT).show()

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

        val dbRef = FirebaseDatabase.getInstance().getReference("Product")
        var query : Query = dbRef.orderByChild("name").startAt("").endAt("")
        query.addListenerForSingleValueEvent(valueEventListener)


//        drawer.openDrawer(GravityCompat.START)

        //TEMP
        val db_topSellingRecView = abc.findViewById<RecyclerView>(R.id.db_topSellingRecView)

        db_topSellingRecView.adapter = prodAdapter

        return abc
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            prodList.clear()
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    val prod: Product? = snapshot.getValue(Product::class.java)
                    if(prod!=null){
                        prodList.add(prod)
                        idList.add(snapshot.key.toString())
//                        Toast.makeText(this@MainActivity, snapshot.key.toString(), Toast.LENGTH_SHORT).show()
                        fetchImage(snapshot.key.toString())
                    }else{
                        Toast.makeText(abc.context, "Snapshot returned null !", Toast.LENGTH_SHORT).show()
                    }
                }
                prodAdapter.notifyDataSetChanged()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
    fun fetchImage(id: String){
        var storRef = FirebaseStorage.getInstance().getReference("ProductImages")
        val localFile: File = File(abc.context.filesDir.path+"/ProductImages/"+id)
//        val localFilePath: File = File(/ProductImages")

//        localFilePath.mkdirs()
//        val localFile = File(localFilePath, id+".jpg")
//        Toast.makeText(this@MainActivity, localFile.path.toString(), Toast.LENGTH_SHORT).show()
        Log.d("ABC", localFile.absolutePath)
        storRef.child(id+".jpg").getFile(localFile)
            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
//                Toast.makeText(this@MainActivity, "Successfully fetched images", Toast.LENGTH_SHORT).show()
                // Successfully downloaded data to local file
//                var newFile = File(filesDir.path+"/ProductImages/"+id)
//                if(newFile.exists()){
//                    Log.d("ABC", "Exists")
//                }else{
//                    Log.d("ABC", "Not Exists")
//                }
//                var myBitmap = BitmapFactory.decodeFile(newFile.absolutePath)
//                if (myBitmap==null){
//                    Log.d("ABC", "Error aavi2"+filesDir+"/ProductImages/"+id)
//                }
//                micBtn.setImageBitmap(myBitmap)

                // ...
            }).addOnFailureListener(OnFailureListener {
                // Handle failed download
                Toast.makeText(abc.context, "Failed to store image file !"+it.message, Toast.LENGTH_SHORT).show()
                // ...
            })
    }
}
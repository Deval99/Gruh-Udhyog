package com.hunar.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CategoriesFrag(val supportFragmentManager: FragmentManager?, val dashboard: Dashboard?) : Fragment(){
    constructor() : this(null, null){
    }

    var prodList = ArrayList<Seller>()
    var idList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.db_categories_list_frag, container, false)
        var catFrag_recViewV = view.findViewById<RecyclerView>(R.id.catFrag_recView)
        if(dashboard==null){
            return view
        }
        if(supportFragmentManager==null){
            return view
        }

        val list: ArrayList<Categories> = ArrayList()
        var db  = FirebaseFirestore.getInstance()
        db.collection("categories")
            .orderBy("categoryName")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        list.add(
                            Categories(
                                document["categoryName"].toString(),
                                "DESC_PENDING",
                                document["categoryImage"].toString()
                            )
                        )

                        Log.d("TAG", document.id + " => " + document["categoryName"])
                    }
                    catFrag_recViewV.adapter = CategoryAdapter(
                        supportFragmentManager,
                        list,
                        dashboard,
                        "CL"
                    )
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        catFrag_recViewV.setHasFixedSize(true)
        //recView.setLayoutManager(new LinearLayoutManager(this));
        //recView.setLayoutManager(new LinearLayoutManager(this));
        val mLayoutManager = GridLayoutManager(dashboard, 2)

        catFrag_recViewV.layoutManager = mLayoutManager

        return view
    }
}
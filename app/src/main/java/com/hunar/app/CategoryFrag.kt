package com.hunar.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Index
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.db_category_frag.*
import kotlinx.android.synthetic.main.db_category_frag.view.*
import java.util.*

class CategoryFrag(val supportFragmentManager: FragmentManager?, val str: String, val aobj: Categories?) : Fragment(){

    constructor(str: String) : this(null, str, null){

    }

    constructor() : this(null, "", null){

    }
//    constructor() : this(Categories("null", "null", "https://firebasestorage.googleapis.com/v0/b/hunar-95d9c.appspot.com/o/404error.jpeg?alt=media&token=90f6b4fc-fc66-47c0-ac38-67c3b53f8f54")){
//
//    }

    var prodList = ArrayList<Seller>()
    var idList = ArrayList<String>()
    lateinit var obj : Categories




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.db_category_frag, container, false)

        if(activity==null){
            return view
        }
        if(activity?.intent == null){
            return view
        }
        if(supportFragmentManager==null){
            return view
        }

        var db = FirebaseFirestore.getInstance()

        if(aobj == null) {
            db.collection("categories").whereEqualTo("categoryName", str).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Log.e("TAG", "No records found!!")
                        obj = Categories(
                            "null",
                            "null",
                            "https://firebasestorage.googleapis.com/v0/b/hunar-95d9c.appspot.com/o/404error.jpeg?alt=media&token=90f6b4fc-fc66-47c0-ac38-67c3b53f8f54"
                        )
                    } else {
                        var doc = it.documents[0]
                        Log.e("TAG", "else1")
                        obj = Categories(
                            doc.get("categoryName").toString(),
                            doc.get("categoryDesc").toString(),
                            doc.get("categoryImage").toString()
                        )
                    }
                    code(view)
                }
                .addOnFailureListener {
                    Log.e("TAG", "Error getting data !")
                    obj = Categories(
                        "null",
                        "null",
                        "https://firebasestorage.googleapis.com/v0/b/hunar-95d9c.appspot.com/o/404error.jpeg?alt=media&token=90f6b4fc-fc66-47c0-ac38-67c3b53f8f54"
                    )
                    code(view)
                }
        }else{
            Log.e("TAG", "else")
            obj = aobj
            code(view)
        }

        return view
    }

    fun code(view: View){
        var catActTitleV = view.findViewById<TextView>(R.id.catActTitle)
        var catActTitleImageV : androidx.appcompat.widget.AppCompatImageView?
        try {
            catActTitleImageV =
                view.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.catActTitleImage) as androidx.appcompat.widget.AppCompatImageView
            catActTitleV?.text = obj.title

            val imageUrl = GlideImageURL()
            imageUrl.imageUrl = obj.image
            Glide.with(view.context as Context).load(imageUrl.imageUrl).into(catActTitleImageV)
        }catch (e: Exception){
            Log.e("TAG", "Failed to fetch image !" + e.toString())
        }


        val client = Client("OVGQESHJVC", "bae0c9e4a2369b45bb0ca74f41132849")
        val index: Index = client.getIndex("sellers")



        val completionHandler: CompletionHandler =
            CompletionHandler { content, error ->
                if(content==null){
                    Log.d("TAG", "No Rows Found")
                    return@CompletionHandler
                }

                if(view == null){
                    return@CompletionHandler
                }

                var count = Integer.valueOf(content.get("nbHits").toString()) - 1

                Log.e("Records Found: ", count.toString())
                if(count == -1){
                    var catActNoRecFoundV = view.findViewById<TextView>(R.id.catActNoRecFound)
                    catActNoRecFoundV?.visibility = View.VISIBLE
                }
                for (x in 0..count){
                    idList.add(
                        content.getJSONArray("hits").getJSONObject(x).get("objectID").toString()
                    )
//                    Log.e("TAG", "Request Completed   "+ content.getJSONArray("hits").getJSONObject(x).get("objectID"))
                    var pi = content.getJSONArray("hits").getJSONObject(x);
                    Log.e(
                        "TAG", "Request Completed   " + content.getJSONArray("hits").getJSONObject(
                            x
                        ).get("sellerName")
                    )
                    lateinit var sellerAddr : String
                    lateinit var sellerCat : String;
                    lateinit var sellerCity : String;
                    lateinit var sellerDesc : String;
                    lateinit var sellerName : String;
                    lateinit var sellerPhone : String;
                    lateinit var sellerState : String;

                    try {
                        sellerAddr = pi.get("sellerAddress").toString()
                    }catch (e: Exception){ sellerAddr = "NULL" }
                    try {
                        sellerCat = pi.get("sellerCategories").toString()
                    }catch (e: Exception){ sellerCat = "NULL" }
                    try {
                        sellerCity = pi.get("sellerCity").toString()
                    }catch (e: Exception){ sellerCity = "NULL" }
                    try {
                        sellerDesc = pi.get("sellerDescription").toString()
                    }catch (e: Exception){ sellerDesc = "NULL" }
                    try {
                        sellerName = pi.get("sellerName").toString()
                    }catch (e: Exception){ sellerName = "NULL" }
                    try {
                        sellerPhone = pi.get("sellerPhone").toString()
                    }catch (e: Exception){ sellerPhone = "NULL" }
                    try {
                        sellerState = pi.get("sellerState").toString()
                    }catch (e: Exception){ sellerState = "NULL" }

                    prodList.add(
                        Seller(
                            content.getJSONArray("hits").getJSONObject(x).get("objectID")
                                .toString(),
                            sellerAddr,
                            sellerCat,
                            sellerCity,
                            sellerDesc,
                            sellerName,
                            sellerPhone,
                            sellerState
                        )
                    )
                }
                var ada = SellerAdapter(
                    supportFragmentManager,
                    view.context as Context,
                    prodList,
                    idList,
                    (view.context as Context).filesDir.path
                )
                view.actCat_recView.adapter = ada
                view.actCat_recView.layoutManager = LinearLayoutManager(view.context)
            }
        index.searchAsync(com.algolia.search.saas.Query(obj.title), completionHandler);
    }
}
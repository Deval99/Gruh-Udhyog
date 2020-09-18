package com.hunar.app

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Index
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.db_search_frag.*
import kotlinx.android.synthetic.main.seller_frag.*
import kotlinx.android.synthetic.main.seller_frag.view.*
import java.util.ArrayList

class SellerFrag(val supportFragmentManager: FragmentManager?,val seller: Seller?) : Fragment(){

    var prodList = ArrayList<Product>()
    var idList = ArrayList<String>()

    constructor(): this(null, null){

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.seller_frag, container, false)

        var catFrag_recViewV = view.findViewById<RecyclerView>(R.id.catFrag_recView)
        if(supportFragmentManager==null){
            return view
        }
        if(seller==null){
            return view
        }

        view.sellerFragFilterBtn.setOnClickListener{
            Log.e("pro", "HELLO!")
        }

        var db = FirebaseFirestore.getInstance()

//        var sellerFragNameV = view.findViewById<TextView>(R.id.sellerFragName)
//        sellerFragNameV.text = seller.sellerName

        db.collection("products").whereEqualTo("userID", seller.sellerId).get()
            .addOnSuccessListener {
                if(it.size() == 0){
                    Log.e("TAG", "This Seller has no products !")
                    sellerFragNoProd.visibility = View.VISIBLE
                }
                for(doc in it.documents){
                    Log.e("TAG", doc.id.toString())
                    idList.add(doc.id)
                    var prodCategory = doc.get("productCategory").toString()
                    var prodName = doc.get("productName").toString()
                    var prodImg = doc.get("productURL").toString()
                    var prodDesc = doc.get("productDescription").toString()
                    var prodPrice = doc.get("productPrice").toString()
                    var keywords = doc.get("productKeywords").toString()
                    var sellerId = doc.get("userID").toString()

                    prodList.add(Product(prodCategory, prodName, prodImg, prodDesc, prodPrice, keywords, sellerId, 0))
                }
                sellerFragRecView.adapter = ProductAdapter(supportFragmentManager, view, prodList, idList, view.context.filesDir.path, ArrayMap<String, Int>())
                sellerFragRecView.layoutManager = LinearLayoutManager(view.context)
            }

        return view
    }
}
package com.hunar.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Index
import kotlinx.android.synthetic.main.db_search_frag.*
import java.util.ArrayList

class SearchFrag(val supportFragmentManager : FragmentManager?,val searchString : String) : Fragment(){

    constructor() : this(null,""){

    }

    var prodList = ArrayList<Seller>()
    var idList = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.db_search_frag, container, false)

        if(activity==null){
            return view
        }
        if(activity?.intent == null){
            return view
        }
        if(supportFragmentManager==null){
            return view
        }



        var searchActTitle = view.findViewById<TextView>(R.id.searchActTitle)

        if(searchString == ""){
            searchActTitle.text = "Showing All Sellers"
        }else {
            searchActTitle.text = "$searchString"
        }
//        var db = FirebaseFirestore.getInstance()
//        db.collection("products").whereEqualTo("productName", searchString).get()
//            .addOnSuccessListener {
//                if(it.size()==0){
//                    var tst = Toast.makeText(this@SearchActivity, "No Records Found !!", Toast.LENGTH_SHORT)
//                    tst.setGravity(Gravity.TOP, 0, 300)
//                    tst.show()
//                    finish()
//                }
//                Log.d("TAG", it.size().toString())
//                for(x in it.documents){
//                    idList.add(x.id)
//                    prodList.add(Product(x.get("productCategory").toString(), x.get("productName").toString(), x.get("prodImg").toString(), x.get("prodDesc").toString(), x.get("prodPrice").toString(), x.get("productKeywords").toString(), x.get("userID").toString()))
//                }
//                var ada = ProductAdapter(this, prodList, idList, filesDir.path)
//                searchAct_recView.adapter = ada
//                searchAct_recView.layoutManager = LinearLayoutManager(this)
//
//                Log.d("TAG", ada.itemCount.toString())
//            }
//            .addOnFailureListener {
//                Log.e("TAG", it.toString())
//            }
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

                    prodList.add(Seller(content.getJSONArray("hits").getJSONObject(x).get("objectID").toString(), sellerAddr, sellerCat, sellerCity, sellerDesc, sellerName, sellerPhone, sellerState))
                }
                var ada = SellerAdapter(supportFragmentManager, view.context, prodList, idList, view.context.filesDir.path)

                var recView = view.findViewById<RecyclerView>(R.id.searchAct_recView)
                recView.adapter = ada
                recView.layoutManager = LinearLayoutManager(view.context)
            }

        index.searchAsync(com.algolia.search.saas.Query(searchString), completionHandler);

        return view
    }
}
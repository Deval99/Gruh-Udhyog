package com.hunar.app


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.cart_frag.*
import kotlinx.android.synthetic.main.db_category_frag.view.*
import kotlinx.android.synthetic.main.seller_frag.*
import java.util.*


class CartFrag(val supportFragmentManager: FragmentManager?, val qtyMap: ArrayMap<String, Int>?, val prodMap: ArrayMap<String, Product>?, val seller : Seller? ) : Fragment(){
    lateinit var db : FirebaseFirestore
    constructor(): this(null, null, null, null){

    }

    fun placeOrder(orderInfo: HashMap<String, Any>){
        db.collection("customerOrders").add(orderInfo)
            .addOnSuccessListener { Log.d("TAG", "Order Placed Successfully") }
            .addOnFailureListener { Log.d("TAG", "Error Placing Order : $it") }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.cart_frag, container, false)

        view.setOnClickListener{
            Log.d("TAG", "Ignore this")
        }

        if(supportFragmentManager==null){
            return view
        }
        if(qtyMap==null){
            Log.e("TAG", "No product UID and Quantity found !")
            return view
        }
        if(prodMap==null){
            Log.e("TAG", "No product found !")
            return view
        }
        if(seller==null){
            Log.e("TAG", "No Seller found !")
            return view
        }
        val spinner: Spinner = view.findViewById(R.id.spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.paymethods,
            R.layout.spinner_text_design
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_text_design)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        var prodList= ArrayList<Product>()
        var idList= ArrayList<String>()
        var qtyList = ArrayList<Int>()
        for(x in prodMap){
            if(qtyMap.get(x.key)==null)
                break

            idList.add(x.key)
            prodList.add(x.value)
            qtyList.add(qtyMap[x.key]!!)
        }

        var recView = view.findViewById<RecyclerView>(R.id.cart_frag_recview)
        recView.adapter = CartProdAdapter(supportFragmentManager, view, prodList, idList, qtyList, view.context.filesDir.path, seller)
        recView.layoutManager = LinearLayoutManager(view.context)

        var productUid : String = ""
        var productQuantity : String = ""
        var flag=0

        for(x in qtyMap){

            if(flag==0){
                productUid = x.key
                productQuantity = x.value.toString()
                flag=1
            }else{
                productUid += ","+x.key
                productQuantity += ","+x.value.toString()
            }
        }

        Log.e("ABC", productUid)
        Log.e("ABC", productQuantity)

        var billingMethod = "COD"
        var shippingAddress = "\"Varad\"  358-c, 12th Street (right side), Raijinagar, Junagadh-362001"

        db = FirebaseFirestore.getInstance()
        var user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            Log.e("TAG", "User is null")
            return view
        }
        Log.d("TAG", "A")
        var sharedPref = view.context.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)

        var loginNum = sharedPref.getString("loginNum", null)
        if(loginNum==null){
            Log.e("TAG", "Login Number is null !")
            return view
        }

        var cartTitle = view.findViewById<TextView>(R.id.cart_frag_title)
        cartTitle.text = seller.sellerName
        var userName = sharedPref.getString("userName", null)
        if(userName==null){
            val builder = AlertDialog.Builder(view.context)
            builder.setMessage("Please fill user details from Dashboard -> Upper right corner -> User icon")
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        return@OnClickListener
                    })
            val alertDialog = builder.create()
            alertDialog.show()
        }else {
            var customerOrders = hashMapOf<String, Any>(
                "customerUid" to user.uid,
                "userID" to "",
                "productUid" to productUid,

                "sellerName" to "",
                "productName" to "",
                "productCategory" to "",
                "productPrice" to "",
                "customerName" to userName,

                "quantity" to productQuantity,
                "orderDate" to FieldValue.serverTimestamp(),
                "deliveryDate" to "",
                "billingMethod" to billingMethod,
                "shippingAddress" to shippingAddress,

                "orderStatus" to "pending",
                "orderTrack" to "pending",
                "userNumber" to loginNum
            )

            view.findViewById<TextView>(R.id.cartSubmitBtn).setOnClickListener {

                db.collection("products").document(productUid.split(",")[0]).get()
                    .addOnSuccessListener() { productData ->
                        if (productData != null) {
                            customerOrders["productName"] =
                                productData.get("productName").toString()
                            customerOrders["productCategory"] =
                                productData.get("productCategory").toString()
                            customerOrders["productPrice"] =
                                productData.get("productPrice").toString()
                            customerOrders["userID"] = productData.get("userID").toString()
                            var f = false
                            var size = productUid.split(",").size
                            size -= 1


                            productUid.split(",").forEach { productId ->
                                if (f) {

                                    db.collection("products").document(productId).get()
                                        .addOnSuccessListener { productDataRem ->
                                            customerOrders["productName"] =
                                                customerOrders["productName"].toString() + "," + productDataRem.get(
                                                    "productName"
                                                ).toString()
                                            customerOrders["productCategory"] =
                                                customerOrders["productCategory"].toString() + "," + productDataRem.get(
                                                    "productCategory"
                                                ).toString()
                                            customerOrders["productPrice"] =
                                                customerOrders["productPrice"].toString() + "," + productDataRem.get(
                                                    "productPrice"
                                                ).toString()
                                            size -= 1
                                            if (size == 0) {
                                                db.collection("sellers")
                                                    .document(customerOrders["userID"].toString())
                                                    .get()
                                                    .addOnSuccessListener() { sellerData ->
                                                        if (sellerData != null) {
                                                            customerOrders["sellerName"] =
                                                                sellerData.get("sellerName")
                                                                    .toString()
                                                            Toast.makeText(view.context,
                                                                "ORDER PLACED!!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            placeOrder(customerOrders)
//================================================================
//      HERE!
                                                        } else {
                                                            Log.e(
                                                                "TAG",
                                                                "Data Snapshot of seller is null !"
                                                            )
                                                        }

                                                    }
                                                    .addOnFailureListener() {
                                                        Log.e("Error", it.toString())
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.e("TAG", "Failed fetching other products")
                                        }
                                }
                                f = true
                            }

                        } else {
                            Log.e("TAG", "Data Snapshot of product is null !")
                        }

                    }
                    .addOnFailureListener() {
                        Log.e("TAG", it.toString())
                    }
            }
        }

        return view
    }
}
package com.hunar.app

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SubmitOrder : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    fun placeOrder(orderInfo: HashMap<String, Any>){
        db.collection("customerOrders").add(orderInfo)
            .addOnSuccessListener { Log.d("TAG", "Order Placed Successfully") }
            .addOnFailureListener { Log.d("TAG", "Error Placing Order : $it") }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        db = FirebaseFirestore.getInstance()

        var user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            Log.e("TAG", "User is null")
            finish()
            return
        }

        var sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)

        var loginNum = sharedPref.getString("loginNum", null)
        if(loginNum==null){
            Log.e("TAG", "Login Number is null !")
            finish()
            return
        }

        var userName = sharedPref.getString("userName", null)
        if(userName==null){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Please fill user details from Dashboard -> Upper right corner -> User icon")
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                        finish()
                    })
            val alertDialog = builder.create()
            alertDialog.show()
        }else {

            setContentView(R.layout.activity_submit_order)

            //UserID
            //LoginNum


            var productUid = "NZZ8bTdDOmKyqiSU5IyK"
            var productQuantity = 1
            var billingMethod = "Cash"
            var shippingAddress = "Gokuldham Society, nepal"


            //Upload new seller
//         val sellerObj = hashMapOf(
//            "sellerName" to "B.Tech Gruh udhyog",
//            "sellerCategories" to "Craft, Food & Snacks",
//            "sellerCity" to "Junagadh",
//            "sellerState" to "Gujarat",
//            "sellerAddress" to "Maninagar, Ahmedabad, Nepal",
//            "sellerPhone" to "95124928692",
//            "sellerDescription" to "ABCDEFGH"
//        )
//        var sellerRef = db.collection("seller")
////
//        sellerRef.document(user.uid)
//            .set(sellerObj)
//            .addOnSuccessListener { Log.d("TAG", "Successfully added seller : "+"B.Tech Gruh Udhyog") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

//      UPLOAD USER
//        val userObj = hashMapOf(
//            "INFO" to "THIS IS A TEMP USER SCHEMA",
//            "userName" to "Deval",
//            "userPhoneNum" to loginNum
//        )
//        db.collection("users").document(user.uid)
//            .set(userObj)
//            .addOnSuccessListener { Log.d("TAG","User added") }
//            .addOnFailureListener { Log.d("TAG","User add failed") }

//      MAIN -- CustomerOrders
            var customerOrders = hashMapOf<String, Any>(
                "customerUid" to user.uid,
                "userID" to "",
                "productUid" to productUid,

                "sellerName" to "",
                "productName" to "",
                "productCategory" to "",
                "productPrice" to "",
                "customerName" to "",

                "quantity" to productQuantity,
                "orderDate" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "deliveryDate" to "",
                "billingMethod" to billingMethod,
                "shippingAddress" to shippingAddress,

                "orderStatus" to "pending",
                "orderTrack" to "pending"
            )

            db.collection("products").document(productUid).get()
                .addOnSuccessListener() { productData ->
                    if (productData != null) {
                        customerOrders["productName"] = productData.get("productName").toString()
                        customerOrders["productCategory"] =
                            productData.get("productCategory").toString()
                        customerOrders["productPrice"] = productData.get("productPrice").toString()
                        customerOrders["userID"] = productData.get("userID").toString()

                        db.collection("sellers").document(customerOrders["userID"].toString())
                            .get()
                            .addOnSuccessListener() { sellerData ->
                                if (sellerData != null) {
                                    customerOrders["sellerName"] =
                                        sellerData.get("sellerName").toString()
                                    placeOrder(customerOrders)
                                } else {
                                    Log.e("TAG", "Data Snapshot of seller is null !")
                                }
                            }
                            .addOnFailureListener() {
                                Log.e("Error", it.toString())
                            }
                    } else {
                        Log.e("TAG", "Data Snapshot of product is null !")
                    }
                }
                .addOnFailureListener() {
                    Log.e("TAG", it.toString())
                }

//class Product(val prodCategory: String, val prodName : String, val prodImg: String, val prodDesc: String, val prodPrice: String, val keywords: kwArray, val sellerId: String, val delivered: String, val verif: String, val rating: ProdReview){
//        var product = hashMapOf(
//            "productName" to "Khakhra",
//            "productCategory" to "Food & Snacks",
//            "productDescription" to "Masala",
//            "productPrice" to 44.44,
//            "productKeywords" to "Food, Khakhra, Snacks, Gujarati",
//            "sellerUid" to sellerUid,
//            "verified" to "PENDING",
//            "ratingUid" to "fjihiFANAWEFN8EA3q2wr"
//        )
//        var prodRef = db.collection("products")
//
//        prodRef
//            .add(product)
//            .addOnSuccessListener { Log.d("TAG","Successfully added Khakhra !") }
//            .addOnFailureListener{ Log.e("TAG", "Failed to add khakhra !") }
//
//        CustomerOrder{
//            CustomerUid
//            sellerUid from seller
//
//            SellerName
//            SellerCategories
//            productid from products
//            productName
//            ProductCategories
//            productPrice
//            int Quantity
//                    int DeliveryDate
//        }
//
//        khakra


//        val seller = hashMapOf(
//            "sellerName" to "B.Tech Gruh Udhyog",
//            "sellerCategories" to "Food & Snacks, Clothing",
//            "sellerAddress" to "Chandkheda, nepal",
//            "sellerCity" to "Ahmedabad",
//            "sellerState" to "Gujarat",
//            "sellerDescription" to "We make fresh food",
//            "sellerPhoneNum" to "+911234567890"
//        )
//        db.collection("sellers").add(seller)
//            .addOnSuccessListener { Log.d("TAG", "Seller Added!") }
//            .addOnFailureListener { Log.d("TAG", "Seller adding failed!") }


//        val order = hashMapOf(
//            "SellerName" to SellerName.text.toString(),
//            "SellerCategories" to SellerCategories.text.toString(),
//            "productid" to productid.text.toString(),
//            "productName" to productName.text.toString(),
//            "ProductCategories" to ProductCategories.text.toString(),
//            "productPrice" to productPrice.text.toString(),
//            "Quantity" to Quantity.text.toString(),
//            "DeliveryDate" to DeliveryDate.text.toString()
//        )
////
//        var id = user.uid;
//
//        db.collection("cities").document(id)
//            .set(order)
//            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
        }
    }
}
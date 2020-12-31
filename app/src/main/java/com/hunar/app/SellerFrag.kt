package com.hunar.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.CompletionHandler
import com.algolia.search.saas.Index
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.db_search_box_layout.*
import kotlinx.android.synthetic.main.db_search_frag.*
import kotlinx.android.synthetic.main.seller_frag.*
import kotlinx.android.synthetic.main.seller_frag.editSearch
import kotlinx.android.synthetic.main.seller_frag.view.*
import java.util.*

open class ImageView

class SquareImg() : ImageView(){
    fun onMeasure(){

    }
}
class SellerFrag(val supportFragmentManager: FragmentManager?,val seller: Seller?) : Fragment(){

    var prodList = ArrayList<Product>()
    var idList = ArrayList<String>()
    lateinit var view2 : View

    constructor(): this(null, null){

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view2 = inflater.inflate(R.layout.seller_frag, container, false)

        var catFrag_recViewV = view2.findViewById<RecyclerView>(R.id.catFrag_recView)
        if(supportFragmentManager==null){
            return view
        }
        if(seller==null){
            return view
        }

        view2.sellerFragFilterBtn.setOnClickListener{
            Log.e("pro", "HELLO!")
        }

        var db = FirebaseFirestore.getInstance()

        var sellerFragNameV = view2.findViewById<TextView>(R.id.sellerFragTitle)
        sellerFragNameV.text = seller.sellerName

        var storageRef = FirebaseStorage.getInstance().reference
        storageRef.child("UserProfilePicture/"+seller.sellerId+"/userProfilePhoto").getDownloadUrl()
            .addOnSuccessListener{
                Glide.with(this).load(it).into(sellerDp)
            }
            .addOnFailureListener{
                Log.e("ERR", "Image Fetch Error ! $it")
            }


        if(prodList.count()==0){
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
                    sellerFragRecView.adapter = ProductAdapter(supportFragmentManager, view2, prodList, idList, view2.context.filesDir.path, seller)
                    sellerFragRecView.layoutManager = LinearLayoutManager(view2.context)
                }
                .addOnFailureListener{
                    Log.e("TAG", "ERROR -- $it")
                }
        }else{
            var recView = view2.findViewById<RecyclerView>(R.id.sellerFragRecView)
            recView.adapter = ProductAdapter(supportFragmentManager, view2, prodList, idList, view2.context.filesDir.path, seller)
            recView.layoutManager = LinearLayoutManager(view2.context)
        }

        view2.findViewById<EditText>(R.id.editSearch).requestFocus()

        var searchBtn = view2.findViewById<View>(R.id.searchBtn)
        searchBtn.setOnClickListener(){
            supportFragmentManager.popBackStack("OverlapFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val imm =
                view2.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)

            // ====================================================================================
            // For setting margins of constraint layout to 0, no longer used

            //                    titleBarDb.visibility = View.INVISIBLE
            //                    val frameLayout: FrameLayout =
            //                        findViewById<View>(R.id.db_frag) as FrameLayout
            //
            //                    val params: ConstraintLayout.LayoutParams =
            //                        frameLayout.getLayoutParams() as ConstraintLayout.LayoutParams
            //                    params.setMargins(0, 0, 0, 0)
            //                    frameLayout.setLayoutParams(params)
            // ====================================================================================

            var frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, SearchFrag(
                    supportFragmentManager,
                    editSearch.text.toString()
                )
            )
            frag.addToBackStack("SearchFrag")
            frag.commit()

        }
        if (ContextCompat.checkSelfPermission(
                view2.context,
                Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
        } else {
            var sharedPref = view2.context.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
            if(sharedPref.getString("mic", null) != "false") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askPerm("Permission", getString(R.string.askMicP))
                } else {
                    val builder = AlertDialog.Builder(view2.context)
                    builder.setTitle("Permission")
                    builder.setMessage(getString(R.string.askMic))
                    builder.setPositiveButton("Ok") { dialogInterface, which ->
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }else{
                Toast.makeText(view2.context, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
            }
        }
        var editSearch = view2.findViewById<EditText>(R.id.editSearch)
        editSearch.setOnClickListener{
            editSearch.hint = getString(R.string.searchTxt)
        }
        val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(view2.context);
        val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        // ====================================================================================
        //FOR DIFFERENT LANGUAGES
        if (resources.configuration.locale.toString().equals("gu")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "gu-IN");
        } else if (resources.configuration.locale.toString().equals("hi")){
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        } else {
            mSpeechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            );
        }
        // ====================================================================================

        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                //getting all the matches
                val matches = bundle
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                //displaying the first match
                if (matches != null) editSearch.setText(matches[0])
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        view2.findViewById<ImageButton>(R.id.sellerMicBtn).setOnTouchListener(
            @SuppressLint("ClickableViewAccessibility")
            fun(view: View, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {
                        mSpeechRecognizer.stopListening()
                        editSearch.hint = "Wait or Hold mic button and speak"
                    }
                    MotionEvent.ACTION_DOWN -> {
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                        editSearch.setText("")
                        editSearch.hint = "Listening"
                    }
                    MotionEvent.ACTION_BUTTON_RELEASE -> {
                        editSearch.hint = "Search"
                    }
                }
                return false
            }
        )

        return view2
    }
    @RequiresApi(Build.VERSION_CODES.M)
    public fun askPerm(p0: String, p1: String){
        if(view2==null){

            return
        }else if(view2.context == null){
            return
        }
        val builder = AlertDialog.Builder(view2.context)
        builder.setTitle(p0)
        builder.setMessage(p1)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            val imm =
                view2.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
        builder.setNegativeButton("No"){ dialogInterface, which: Int ->
            Toast.makeText(view2.context, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
            var sharedPref = view2.context.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
            var sharedPrefEdit = sharedPref.edit()
            sharedPrefEdit.putString("mic", "false")
            sharedPrefEdit.apply()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
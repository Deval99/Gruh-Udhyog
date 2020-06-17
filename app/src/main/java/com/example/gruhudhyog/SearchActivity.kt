package com.example.gruhudhyog

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_search.editSearch
import kotlinx.android.synthetic.main.activity_search.searchBtn
import kotlinx.android.synthetic.main.activity_search.*
import java.io.File
import java.util.*

class SearchActivity : AppCompatActivity() {
    lateinit var dbRef : DatabaseReference
    var prodList = ArrayList<Product>()
    var idList = ArrayList<String>()
    lateinit var prodAdapter : ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        progressBar.visibility = View.INVISIBLE
        searchBtn.setOnClickListener{
            var str : String = editSearch.text.toString()
            Log.d("ABC", str.trim())
            var query : Query = dbRef.orderByChild("name").startAt(str.trim().toLowerCase().capitalize()).endAt(str.trim().toLowerCase().capitalize()+"\uf8ff")

            query.addListenerForSingleValueEvent(valueEventListener)
            searchBtn.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recView.layoutManager = llm

        prodAdapter =   ProductAdapter(this, prodList, idList, filesDir.path.toString())
        recView.adapter = prodAdapter
        dbRef = FirebaseDatabase.getInstance().reference.child("Product")

        //FOR SEARCH MIC
        //===============================================================================================================================================
        //===============================================================================================================================================

        if (ContextCompat.checkSelfPermission(this@SearchActivity,
                Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPerm("Permission", getString(R.string.askMicP))
            } else {
                val builder = AlertDialog.Builder(this@SearchActivity)
                builder.setTitle("Permission")
                builder.setMessage(getString(R.string.askMic))
                builder.setPositiveButton("Ok") { dialogInterface, which ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }

        val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        //FOR DIFFERENT LANGUAGES
        if (resources.configuration.locale.toString().equals("gu")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "gu-IN");
        } else if (resources.configuration.locale.toString().equals("hi")){
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        } else {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        }
        //=====

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

        editSearch.setOnClickListener{
            editSearch.hint = getString(R.string.searchTxt)
        }
        micBtn.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    mSpeechRecognizer.stopListening()
                    editSearch.hint = "Hold and speak"
//                    Toast.makeText(this, "NOW SEARCH !", Toast.LENGTH_SHORT).show()
                    var query : Query = dbRef.orderByChild("name").startAt(editSearch.text.toString()).endAt(editSearch.text.toString()+"\uf8ff")
                    query.addListenerForSingleValueEvent(valueEventListener)
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
            false
        }
        //===============================================================================================================================================
        //===============================================================================================================================================
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
                        Toast.makeText(this@SearchActivity, "Snapshot returned null !", Toast.LENGTH_SHORT).show()
                    }
                    searchBtn.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
                prodAdapter.notifyDataSetChanged()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun fetchImage(id: String){
        var storRef = FirebaseStorage.getInstance().getReference("ProductImages")
        val localFile: File = File(filesDir.path+"/ProductImages/"+id)
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
                Toast.makeText(this, "Failed to store image file !"+it.message, Toast.LENGTH_SHORT).show()
                // ...
            })
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPerm(p0:String, p1:String){
        val builder = AlertDialog.Builder(this@SearchActivity)
        builder.setTitle(p0)
        builder.setMessage(p1)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
        builder.setNegativeButton("No"){ dialogInterface, which: Int ->
            Toast.makeText(this@SearchActivity, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
package com.hunar.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.core.graphics.drawable.DrawableCompat.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.db_search_box_layout.*
import kotlinx.android.synthetic.main.overlap_frag.*
import java.util.*
import java.util.zip.Inflater

class OverlapFrag(val supportFragmentManager : FragmentManager?, val dashboard : Dashboard?) : Fragment(){
    lateinit var db_search_lt : View
    lateinit var db_search : View

    constructor() : this(null, null){

    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.overlap_frag, container, false)
        if(supportFragmentManager==null || dashboard==null){
            return view
        }

        if(view.findViewById<View>(R.id.db_search) == null) {
            var overlap_layoutV = view.findViewById<LinearLayout>(R.id.overlap_layout)
            db_search_lt =
                layoutInflater.inflate(
                    R.layout.db_search_box_layout,
                    overlap_layoutV
                )

//            db_search = view.findViewById<ConstraintLayout>(R.id.db_search)

            view.findViewById<EditText>(R.id.editSearch).requestFocus()

            var searchBtn = view.findViewById<View>(R.id.searchBtn)
            searchBtn.setOnClickListener(){
                supportFragmentManager.popBackStack("OverlapFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    view.context,
                    Manifest.permission.RECORD_AUDIO
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
            } else {
                var sharedPref = view.context.getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)
                if(sharedPref.getString("mic", null) != "false") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dashboard.askPerm("Permission", getString(R.string.askMicP))
                    } else {
                        val builder = AlertDialog.Builder(view.context)
                        builder.setTitle("Permission")
                        builder.setMessage(getString(R.string.askMic))
                        builder.setPositiveButton("Ok") { dialogInterface, which ->
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                }else{
                    Toast.makeText(view.context, getString(R.string.denyPerm), Toast.LENGTH_LONG).show()
                }
            }
            var editSearch = view.findViewById<EditText>(R.id.editSearch)
            editSearch.setOnClickListener{
                editSearch.hint = getString(R.string.searchTxt)
            }
            val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(view.context);
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
            view.findViewById<ImageButton>(R.id.micBtn).setOnTouchListener(
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
        }else{
            db_search.visibility = View.VISIBLE
        }


        return view
    }
}
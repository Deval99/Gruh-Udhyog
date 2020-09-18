package com.hunar.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File


class EditProfile : AppCompatActivity() {
    lateinit var strref : StorageReference
    var imguri: Uri? = null
    var dt : Intent? = null
    lateinit var sharedPref : SharedPreferences
    lateinit var phoneNum : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPref = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE)


        if(sharedPref.getString("loginNum", null) == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Please Login !")
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                        finish()
                    })
            val alertDialog = builder.create()
            alertDialog.show()
            finish()
            return
        }else {

            setContentView(R.layout.activity_edit_profile)

            var db = FirebaseFirestore.getInstance()
            db.collection("users").document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnSuccessListener {
                    if(it.get("userName")!=null){
                        etName.setText(it.get("userName").toString())
                    }
                    if(it.get("userAddr")!=null) {
                        etAddress.setText(it.get("userAddr").toString())
                    }
                }


            phoneNum = sharedPref.getString("loginNum", "abc") as String

            strref = FirebaseStorage.getInstance().getReference("ProfilePic")


            val imgFile: File? = File("/data/user/0/com.hunar.app/files/ProfilePic.jpg")



            if (imgFile != null && imgFile!!.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                val myImage: ImageView =
                    editProfIcon as ImageView
                myImage.setImageBitmap(myBitmap)
            } else {
                val localFile: File = File(filesDir.path + "/ProfilePic.jpg")

                val ref = strref.child(phoneNum + ".jpg")
//                Toast.makeText(this, filesDir.path, Toast.LENGTH_SHORT).show()
                ref.getFile(localFile)
                    .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                        // Successfully downloaded data to local file
                        // ...
                        Toast.makeText(
                            this,
                            "Successfully fetched Profile pic from server !",
                            Toast.LENGTH_SHORT
                        ).show()
                        val myBitmap = BitmapFactory.decodeFile(imgFile?.absolutePath)
                        val myImage: ImageView =
                            editProfIcon as ImageView
                        myImage.setImageBitmap(myBitmap)
                        recreate()
                    }).addOnFailureListener(OnFailureListener {
                        // Handle failed download
                        // ...
                        Toast.makeText(
                            this,
                            "Failed to fetch profile pic from server !",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

            }
            //+==========================================================================================================
            //+==========================================================================================================

            //+==========================================================================================================
            //+==========================================================================================================


            //BUTTON CLICK
            editProfIcon.setOnClickListener {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system OS is < Marshmallow
                    pickImageFromGallery();
                }
            }

            editProfSbmt.setOnClickListener {

                if(etName.text.toString().trim() == "" && etAddress.text.toString().trim() == ""){
                    etName.setError("This is a mandatory field !")
                    etAddress.setError("This is a mandatory field !")
                    return@setOnClickListener
                }else if(etName.text.toString().trim() == ""){
                    etName.setError("This is a mandatory field !")
                    return@setOnClickListener
                }else if(etAddress.text.toString().trim() == ""){
                    etAddress.setError("This is a mandatory field !")
                    return@setOnClickListener
                }


                var db = FirebaseFirestore.getInstance()
                db.collection("users").document(FirebaseAuth.getInstance().uid.toString()).update(
                    hashMapOf("userName" to etName.text.toString()) as Map<String, Any>
                )
                    .addOnSuccessListener { Log.d("TAG", "UserName Updated") }
                    .addOnFailureListener { Toast.makeText(this, "userName add Failed"+it.toString(), Toast.LENGTH_SHORT).show() }
                db.collection("users").document(FirebaseAuth.getInstance().uid.toString()).update(
                    hashMapOf("userAddr" to etAddress.text.toString()) as Map<String, Any>
                )
                    .addOnSuccessListener { Log.d("TAG", "UserAddress Updated") }
                    .addOnFailureListener { Toast.makeText(this, "userAddress add Failed"+it.toString(), Toast.LENGTH_SHORT).show() }
//                uploadFile()
                var sharedPrefEdit = getSharedPreferences("com.hunar.app", Context.MODE_PRIVATE).edit()
                sharedPrefEdit.putString("userName", etName.text.toString())
                sharedPrefEdit.putString("userAddr", etAddress.text.toString())
                sharedPrefEdit.apply()

                uploadFile()
                finish()
            }
        }
    }

    private fun getExt(uri : Uri) : String{
        var cr = contentResolver
        var mtm = MimeTypeMap.getSingleton()
        return mtm.getExtensionFromMimeType(cr.getType(dt?.data as Uri)) as String
    }
    private fun uploadFile(){
        if(imguri==null) {
            return
        }
        var ref : StorageReference = strref.child(phoneNum+"."+getExt(imguri!!))

        val downloadUrl = ref.putFile(imguri!!).addOnSuccessListener{ task -> // Get a URL to the uploaded content
            Toast.makeText(this, "Upload Success !", Toast.LENGTH_SHORT).show()
            ref.downloadUrl
        }
        .addOnFailureListener(OnFailureListener {
            Toast.makeText(this, "Upload Failed !", Toast.LENGTH_SHORT).show()
        })

    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied, You can not upload image !", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imguri = data!!.data
            editProfIcon.setImageURI(data?.data)
            dt=data
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("kemPalty", "Haa")
    }


}

package com.example.gruhudhyog

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
        setContentView(R.layout.activity_edit_profile)

        sharedPref = getSharedPreferences("com.example.gruhudhyog", Context.MODE_PRIVATE)


        if(sharedPref.getString("loginNum", null) == null) {
            Toast.makeText(this, "Please Login !", Toast.LENGTH_SHORT).show()
            finish()
            return
        }else {
            phoneNum = sharedPref.getString("loginNum", "abc") as String

            strref = FirebaseStorage.getInstance().getReference("ProfilePic")


            val imgFile: File? = File("/data/user/0/com.example.gruhudhyog/files/ProfilePic.jpg")

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
                uploadFile()
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
            Toast.makeText(this, "Image Uri Null !", Toast.LENGTH_SHORT).show()
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

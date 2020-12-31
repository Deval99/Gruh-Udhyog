package com.hunar.app

//import android.support.v7.widget.RecyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


class SellerAdapter(
    val supportFragmentManager: FragmentManager?,
    private val mCtx: Context,
    sellerList: List<Seller>,
    idList: List<String>,
    filesDir: String
) :
    RecyclerView.Adapter<SellerAdapter.SellerViewHolder>() {
    private val artistList: List<Seller> = sellerList
    private val idListNew: List<String> = idList
    private val filesDir = filesDir

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerViewHolder {
        val view: View =
            LayoutInflater.from(mCtx).inflate(R.layout.seller_list_layout, parent, false)
        Log.d("TAG", "onCreateViewHolder")
        return SellerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SellerViewHolder,
        position: Int
    ) {



        val id = idListNew[position]
        val seller: Seller = artistList[position]

        Log.d("TAG", seller.sellerName)
        holder.textViewName.setText(seller.sellerName)


        var storageRef = FirebaseStorage.getInstance().reference
        storageRef.child("UserProfilePicture/"+seller.sellerId+"/userProfilePhoto").getDownloadUrl()
            .addOnSuccessListener{
                Glide.with(mCtx).load(it).into(holder.prodImg)
            }
            .addOnFailureListener{
                Log.e("ERR", "Image Fetch Error ! $it")
            }



//        holder.textViewCategory.text = "Category: " + prod.category

//        Log.d("ASD", "X"+prod.prodCategory)
//        holder.textViewName.text = "Name: " + prod.prodName
//        var myBitmap = BitmapFactory.decodeFile(filesDir+"/ProductImages/"+id)
//        if (myBitmap==null){
//            Log.d("ABC", "TAG "+filesDir+"/ProductImages/"+id)
//        }
//        holder.imgView.setImageBitmap(myBitmap)

        holder.prodItem.setOnClickListener{

//            Log.e("TAG", it.findViewById<TextView>(R.id.tv_name).text.toString())
            Log.e("TAG", seller.sellerId)
            if(supportFragmentManager==null){
                return@setOnClickListener
            }
            val frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, SellerFrag(
                    supportFragmentManager,
                    seller
                )
            )
            frag.addToBackStack("SellerFrag")
            frag.commit()
        }
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    inner class SellerViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var textViewName: TextView
        var prodImg: ImageView
        var prodItem: CardView

        init {
            prodImg = itemView.findViewById(R.id.prodImg)
            textViewName = itemView.findViewById(R.id.tv_name)
            prodItem = itemView.findViewById(R.id.prodListItem)
            Log.d("TAG", "Init")
        }
    }

}
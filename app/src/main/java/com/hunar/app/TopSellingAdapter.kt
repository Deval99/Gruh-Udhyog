package com.hunar.app

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
//import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TopSellingAdapter(
    private val mCtx: Context,
    prodList: List<Product>,
    idList: List<String>,
    filesDir: String
) :
    RecyclerView.Adapter<TopSellingAdapter.ProductViewHolder>() {
    private val artistList: List<Product> = prodList
    private val idListNew: List<String> = idList
    private val filesDir = filesDir

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val view: View =
            LayoutInflater.from(mCtx).inflate(R.layout.product_list_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val id = idListNew[position]
        val prod: Product = artistList[position]

        holder.textViewName.setText(prod.prodName)
        holder.textViewCategory.text = "Category: " + prod.prodCategory
//        holder.textViewName.text = "Name: " + prod.prodName
        var myBitmap = BitmapFactory.decodeFile(filesDir+"/ProductImages/"+id)
        if (myBitmap==null){
            Log.d("ABC", "Error ---- "+filesDir+"/ProductImages/"+id)
        }
        holder.imgView.setImageBitmap(myBitmap)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    inner class ProductViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var textViewName: TextView
        var textViewCategory: TextView
        var imgView: ImageView


        init {
            imgView = itemView.findViewById(R.id.prodImg)
            textViewName = itemView.findViewById(R.id.tv_name)
            textViewCategory = itemView.findViewById(R.id.tv_dist)
        }
    }

}
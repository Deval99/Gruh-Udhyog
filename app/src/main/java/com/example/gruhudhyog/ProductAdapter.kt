package com.example.gruhudhyog

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
//import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gruhudhyog.Product
import com.example.gruhudhyog.R

class ProductAdapter(
    private val mCtx: Context,
    prodList: List<Product>,
    idList: List<String>,
    filesDir: String
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val artistList: List<Product> = prodList
    private val idListNew: List<String> = idList
    private val filesDir = filesDir

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val view: View =
            LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val id = idListNew[position]
        val prod: Product = artistList[position]

        holder.textViewName.setText(prod.name)
        holder.textViewCategory.text = "Category: " + prod.category
        holder.textViewName.text = "Name: " + prod.name
        var myBitmap = BitmapFactory.decodeFile(filesDir+"/ProductImages/"+id)
        if (myBitmap==null){
            Log.d("ABC", "Error aavi "+filesDir+"/ProductImages/"+id)
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
            textViewCategory = itemView.findViewById(R.id.tv_category)
        }
    }

}
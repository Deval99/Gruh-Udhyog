package com.hunar.app

//import android.support.v7.widget.RecyclerView

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


//import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton

class ProductAdapter(
    val supportFragmentManager: FragmentManager?,
    val view: View,
    prodList: List<Product>,
    idList: List<String>,
    filesDir: String,
    var map : ArrayMap<String, Int>
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
            LayoutInflater.from(view.context).inflate(R.layout.product_list_layout, parent, false)
        Log.d("TAG", "onCreateViewHolder")
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        if(supportFragmentManager==null){
            return
        }

        val id = idListNew[position]
        val prod: Product = artistList[position]

        Log.d("TAG", prod.prodName)
        holder.textViewName.setText(prod.prodName)

        var prodBottomPrice = view.findViewById<TextView>(R.id.prodBottomPrice)
        var prodNumItems = view.findViewById<TextView>(R.id.prodNumItems)
        var sellerFragBottom = view.findViewById<ConstraintLayout>(R.id.sellerFragBottom)
        var sellerFragRecView = view.findViewById<RecyclerView>(R.id.sellerFragRecView)


      //  var aList = ArrayList<String>()
//        holder.textViewCategory.text = "Category: " + prod.category

//        Log.d("ASD", "X"+prod.prodCategory)
//        holder.textViewName.text = "Name: " + prod.prodName


//        var myBitmap = BitmapFactory.decodeFile(filesDir+"/ProductImages/"+id)
//        if (myBitmap==null){
//            Log.d("ABC", "TAG "+filesDir+"/ProductImages/"+id)
//        }
//        holder.imgView.setImageBitmap(myBitmap)

        holder.prodItem.setOnClickListener{
            Log.e("TAG", it.findViewById<TextView>(R.id.tv_name).text.toString())
        }

        holder.prodListAddBtn.setOnClickListener{
            holder.prodListAddBtn.visibility = View.INVISIBLE
            holder.prodListCount.visibility = View.VISIBLE
            holder.prodListCountText.text = (holder.prodListCountText.text.toString().toInt()+1).toString()
            prodBottomPrice.text = (prodBottomPrice.text.toString().toInt()+prod.prodPrice.toInt()).toString()
            prodNumItems.text = (prodNumItems.text.toString().toInt()+1).toString()

            if(prodNumItems.text.toString()=="1"){
                sellerFragBottom.visibility = View.VISIBLE

                var layoutParams = sellerFragRecView.layoutParams as ConstraintLayout.LayoutParams
                //left, top, right, bottom
                layoutParams.setMargins(spToPx(20, view.context), spToPx(7, view.context),spToPx(20, view.context),spToPx(153, view.context))
                sellerFragRecView.layoutParams = layoutParams

            }
            map[id] = 1
        }

        holder.prodListPlusBtn.setOnClickListener{
            holder.prodListCountText.text = (holder.prodListCountText.text.toString().toInt()+1).toString()
            prodBottomPrice.text = (prodBottomPrice.text.toString().toInt()+prod.prodPrice.toInt()).toString()
            if(map.contains(id)){
                map[id] = map[id]!! + 1
            }else{
                map[id] = 1
            }
        }

        holder.prodListMinBtn.setOnClickListener{
            holder.prodListCountText.text = (holder.prodListCountText.text.toString().toInt()-1).toString()
            prodBottomPrice.text = (prodBottomPrice.text.toString().toInt()-prod.prodPrice.toInt()).toString()



            if(holder.prodListCountText.text.toString() == "0"){
                holder.prodListCount.visibility = View.INVISIBLE
                holder.prodListAddBtn.visibility = View.VISIBLE
                prodNumItems.text = (prodNumItems.text.toString().toInt()-1).toString()


                map.remove(id)
            }
            if(prodNumItems.text.toString()=="0"){

                sellerFragBottom.visibility = View.INVISIBLE

                var layoutParams = sellerFragRecView.layoutParams as ConstraintLayout.LayoutParams
                //left, top, right, bottom
                layoutParams.setMargins(spToPx(20, view.context), spToPx(7, view.context),spToPx(20, view.context),spToPx(84, view.context))
                sellerFragRecView.layoutParams = layoutParams

                map.clear()
            }
            if(map.contains(id) && map[id] != 0) {
                map[id] = map[id]!! - 1
            }
            print(map)
        }

        holder.prodListPrice.text = "₹ "+ prod.prodPrice

        var sellerFragViewCartBtn = view.findViewById<TextView>(R.id.sellerFragViewCartBtn)
        sellerFragViewCartBtn.setOnClickListener{
            var frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CartFrag(supportFragmentManager, map)
            )
            frag.addToBackStack("CartFrag")
            frag.commit()
        }
        var sellerFragViewCartIcon = view.findViewById<View>(R.id.sellerFragViewCartIcon)
        sellerFragViewCartIcon.setOnClickListener{
            var frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CartFrag(supportFragmentManager, map)
            )
            frag.addToBackStack("CartFrag")
            frag.commit()
        }

    }

    fun print(x: ArrayMap<String, Int>){
        Log.e("TAG", "==============")
        for(y in x){
            Log.e("TAG", y.key+"======"+y.value)
        }
    }

    fun spToPx(sp: Int, context: Context): Int {
        var sp2 = sp.toFloat()
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp2,
            context.resources.displayMetrics
        )
            .toInt()
    }


    override fun getItemCount(): Int {
        return artistList.size
    }

    inner class ProductViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var textViewName: TextView
        var imgView: ImageView
        var prodItem: CardView
        var prodListAddBtn : Button
        var prodListCount : LinearLayout
        var prodListMinBtn : View
        var prodListCountText : TextView
        var prodListPlusBtn : View
        var prodListPrice : TextView

        init {
            imgView = itemView.findViewById(R.id.prodListImg)
            textViewName = itemView.findViewById(R.id.tv_name)
            prodItem = itemView.findViewById(R.id.prodListItem)
            prodListAddBtn = itemView.findViewById(R.id.prodListAddBtn)
            prodListCount = itemView.findViewById(R.id.prodListCount)
            prodListMinBtn = itemView.findViewById(R.id.prodListMinBtn)
            prodListCountText = itemView.findViewById(R.id.prodListCountText)
            prodListPlusBtn = itemView.findViewById(R.id.prodListPlusBtn)
            prodListPrice = itemView.findViewById(R.id.prodListPrice)
            Log.d("TAG", "Init")
        }
    }

}
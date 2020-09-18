package com.hunar.app

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hunar.app.CategoryAdapter.ViewHolder

class CategoryAdapter(val supportFragmentManager: FragmentManager?,  private val values: ArrayList<Categories>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(
        layout
    ) {
        // each data item is just a string in this case
        var post_title: TextView = layout.findViewById<View>(R.id.post_title) as TextView
        var post_image: ImageView = layout.findViewById<View>(R.id.post_image) as ImageView

        init {
            //            txtFooter = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    fun add(position: Int, item: Categories) {
        values.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(
            parent.context
        )
        val v: View = inflater.inflate(R.layout.category_card_view, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val name = values[position].title
        val imageUrl = GlideImageURL()
        imageUrl.imageUrl = values[position].image
        if(holder.post_image != null){
            Glide.with(context).load(imageUrl.imageUrl).into(holder.post_image);
        }else{
            Log.e("TAG", "NULL")
        }
        holder.post_title.text = name
        holder.post_title.setOnClickListener{
//            var int = Intent(context, CategoryActivity::class.java) //DELETED CLASS

//            int.putExtra("prod", values[position])
//            context.startActivity(int)

            if(supportFragmentManager==null){
                return@setOnClickListener
            }
            supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CategoryFrag(supportFragmentManager, "", values[position])
            ).commit()
        }

        holder.post_image.setOnClickListener{
//            var int = Intent(context, CategoryActivity::class.java) //DELETED CLASS
//            int.putExtra("prod", values[position])
//            context.startActivity(int)
            if(supportFragmentManager==null){
                return@setOnClickListener
            }
            var frag = supportFragmentManager.beginTransaction().replace(
                R.id.db_frag, CategoryFrag(supportFragmentManager,"", values[position])
            )
            frag.addToBackStack("CategoryFrag")
            frag.commit()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return values.size
    }
}
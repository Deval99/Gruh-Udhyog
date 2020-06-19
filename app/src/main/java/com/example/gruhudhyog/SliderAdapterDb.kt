package com.example.gruhudhyog

//import android.R
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter


public class SliderAdapterDb(context: Context) :
    SliderViewAdapter<SliderAdapterDb.SliderAdapterVH>() {
    private val context: Context = context
    private var mSliderItems: MutableList<SliderItem> = ArrayList()
    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_layout_item, null)


        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(
        viewHolder: SliderAdapterVH,
        position: Int
    ) {
        val sliderItem: SliderItem = mSliderItems[position]
        viewHolder.textViewDescription.text = sliderItem.description
        viewHolder.textViewDescription.textSize = 16f
        viewHolder.textViewDescription.setTextColor(Color.WHITE)
        Glide.with(viewHolder.itemView)
            .load(sliderItem.imageUrl)
            .fitCenter()
            .into(viewHolder.imageViewBackground)

        viewHolder.itemView.setOnClickListener{
            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var itemViewL: View
        var imageViewBackground: ImageView
        var imageGifContainer: ImageView
        var textViewDescription: TextView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageViewBackground.clipToOutline = true
            }
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container)
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider)
            itemViewL = itemView
        }
    }

}


package com.hunar.app
//
//import android.R
//import com.hunar.app.model.Meals

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso


//
class ViewPagerAdapter(
    img: List<SliderClass>,
    context: Context
)  :
    PagerAdapter() {
    private val img: List<SliderClass>
    private val context: Context
    fun setOnItemClickListener(clickListener: ClickListener?) {
        Companion.clickListener = clickListener
    }

    override fun getCount(): Int {
        return img.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.view_pager_layout,
            container,
            false
        )
        val imgThumb =
            view.findViewById<ImageView>(R.id.imgThumb)
        val imgName = view.findViewById<TextView>(R.id.imgName)

        val strMealThumb: String = img[position].img
        Picasso.get().load(strMealThumb).into(imgThumb)

//        Log.d("XYZ", strMealThumb)

        val strMealName: String = img[position].name

        imgName.text = strMealName
        view.setOnClickListener { v: View? ->
            clickListener!!.onClick(
                v,
                position
            )
        }
        container.addView(view, 0)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    interface ClickListener {
        fun onClick(v: View?, position: Int)
    }

    companion object {
        private var clickListener: ClickListener? = null
    }

    init {
        this.img = img
        this.context = context
    }

}
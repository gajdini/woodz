package com.tooz.woodz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tooz.woodz.database.entity.Plank
import androidx.viewpager.widget.PagerAdapter
import com.tooz.woodz.R
import java.util.*


class PlankAdapter(val context: Context, val plankList: List<Plank>) : PagerAdapter() {

    override fun getCount(): Int {
        return plankList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView: View = layoutInflater.inflate(R.layout.plank_item2, container, false)

        val plankWidthTextView: TextView = itemView.findViewById<TextView>(R.id.plank_width) as TextView
        val plankHeightTextView: TextView = itemView.findViewById<TextView>(R.id.plank_height) as TextView
        val plankTypeTextView: TextView = itemView.findViewById<TextView>(R.id.plank_type) as TextView
        val plankGroupTextView: TextView = itemView.findViewById<TextView>(R.id.plank_group) as TextView

        plankWidthTextView.text = plankList.get(position).width.toString()
        plankHeightTextView.text = plankList.get(position).height.toString()
        plankTypeTextView.text = plankList.get(position).type
        plankGroupTextView.text = plankList.get(position).group

        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
package com.tooz.woodz.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tooz.woodz.database.entity.Plank
import androidx.viewpager.widget.PagerAdapter
import com.tooz.woodz.R
import java.util.*


class PlankAdapter(
    val context: Context,
    val plankList: List<Plank>,
    val setUpUi: (promptView: View?) -> Unit
) : PagerAdapter() {

    override fun getCount(): Int {
        return plankList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.i("ScanCallback", "hererere $position")

        val layoutInflater = LayoutInflater.from(context);
        val itemView: View = layoutInflater.inflate(R.layout.plank_item2, container, false)

        val plankNoTextView: TextView = itemView.findViewById(R.id.plank_no)
        val plankWidthTextView: TextView = itemView.findViewById(R.id.plank_width)
        val plankHeightTextView: TextView = itemView.findViewById(R.id.plank_height)
        val plankTypeTextView: TextView = itemView.findViewById(R.id.plank_type)
        val plankGroupTextView: TextView = itemView.findViewById(R.id.plank_group)

        plankNoTextView.text = (position + 1).toString()
        plankWidthTextView.text = plankList.get(position).width.toString()
        plankHeightTextView.text = plankList.get(position).height.toString()
        plankTypeTextView.text = plankList.get(position).type
        plankGroupTextView.text = plankList.get(position).group

        Objects.requireNonNull(container).addView(itemView)

//        setUpUi(itemView)
        //todo
        if (position == 0) {
            setUpUi(itemView)
        } else {
            setUpUi(
                Objects.requireNonNull(container).getChildAt(position - 1)
            )
        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
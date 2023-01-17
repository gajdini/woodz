package com.tooz.woodz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tooz.woodz.R
import com.tooz.woodz.database.entity.Material
import com.tooz.woodz.databinding.MaterialItemBinding

class MaterialAdapter(private val onItemClicked: (Material) -> Unit) : ListAdapter<Material, MaterialAdapter.MaterialViewHolder>(
    DiffCallback
) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Material>() {
            override fun areItemsTheSame(oldItem: Material, newItem: Material): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Material, newItem: Material): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MaterialViewHolder(private var binding: MaterialItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(material: Material) {
            binding.materialNameTextView.text = material.name
            binding.boardNo.text = material.quantity.toString()
            binding.partsNo.text = material.planks.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val viewHolder = MaterialViewHolder(
            MaterialItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        val nextButton = viewHolder.itemView.findViewById<ImageView>(R.id.next_to_planks)
            nextButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }

        val showDetail = viewHolder.itemView.findViewById<ImageView>(R.id.show_details)
        val details = viewHolder.itemView.findViewById<LinearLayout>(R.id.detail_content)
        details.visibility = View.GONE;

        showDetail.setOnClickListener {
            when (showDetail.tag) {
                null, R.drawable.ic_baseline_keyboard_arrow_down_24 -> {
                    showDetail.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    showDetail.tag = R.drawable.ic_baseline_keyboard_arrow_up_24
                    details.visibility = View.VISIBLE;
                }
                R.drawable.ic_baseline_keyboard_arrow_up_24 -> {
                    showDetail.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    showDetail.tag = R.drawable.ic_baseline_keyboard_arrow_down_24
                    details.visibility = View.GONE;
                }
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
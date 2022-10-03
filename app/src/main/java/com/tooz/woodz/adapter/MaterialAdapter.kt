package com.tooz.woodz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
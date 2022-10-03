package com.tooz.woodz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tooz.woodz.database.entity.Plank
import com.tooz.woodz.databinding.PlankItemBinding

class PlankAdapter(private val onItemClicked: (Plank) -> Unit) : ListAdapter<Plank, PlankAdapter.PlankViewHolder>(
    DiffCallback
) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Plank>() {
            override fun areItemsTheSame(oldItem: Plank, newItem: Plank): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Plank, newItem: Plank): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PlankViewHolder(private var binding: PlankItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(plank: Plank) {
            binding.plankWidthTextView.text = plank.width.toString()
            binding.plankHeightTextView.text = plank.height.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlankViewHolder {
        val viewHolder = PlankViewHolder(
            PlankItemBinding.inflate(
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

    override fun onBindViewHolder(holder: PlankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
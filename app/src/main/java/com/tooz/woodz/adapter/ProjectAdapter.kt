package com.tooz.woodz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tooz.woodz.R
import com.tooz.woodz.database.entity.Project
import com.tooz.woodz.databinding.ProjectItemBinding

class ProjectAdapter(private val onItemClicked: (Project) -> Unit) : ListAdapter<Project, ProjectAdapter.ProjectViewHolder>(
    DiffCallback
) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Project>() {
            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ProjectViewHolder(private var binding: ProjectItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.projectNameTextView.text = project.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val viewHolder = ProjectViewHolder(
            ProjectItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        val nextButton = viewHolder.itemView.findViewById<ImageView>(R.id.next_to_materials)
        nextButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
package com.cbc.newsdemo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbc.newsdemo.data.models.Article
import com.cbc.newsdemo.data.models.Primary
import com.cbc.newsdemo.databinding.ItemFollowedPreviewBinding

class FollowedAdapter(private val followedItems: List<Primary>?) : RecyclerView.Adapter<FollowedAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemFollowedPreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemFollowedPreviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        followedItems?.let {
            val article = it[position].item
            holder.binding.apply {
                tvDescription.text = article?.title
            }

            holder.itemView.setOnClickListener {
                article?.let { onItemClickListener?.let { it(article) } }
            }
        }
    }

    override fun getItemCount(): Int {
        return followedItems?.size ?: 0
    }
}
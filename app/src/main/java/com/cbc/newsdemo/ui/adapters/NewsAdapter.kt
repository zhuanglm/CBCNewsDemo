package com.cbc.newsdemo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cbc.newsdemo.databinding.ItemArticlePreviewBinding
import com.cbc.newsdemo.data.models.Article

class NewsAdapter(activity: Context?) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    private val context = activity

    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // tool that will take the two list and tell the differences
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(article.typeAttributes?.imageSmall)
                .into(ivArticleImage)
            tvSource.text = article.typeAttributes?.sectionLabels?.last()
            tvTitle.text = article.title

            tvDescription.text = article.description
            article.typeAttributes?.components?.let {
                tvDescription.text = it.mainContent?.description
            }

            tvPublishedAt.text = article.readablePublishedAt
            tvType.text = article.type
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }

            if(article.type == "contentpackage") {
                article.typeAttributes?.let { it ->
                    it.components?.let {
                        it.primary?.let {
                            val itemAdapter = FollowedAdapter(it)
                            rvFollowedNews.adapter = itemAdapter
                            rvFollowedNews.layoutManager = LinearLayoutManager(context)

                            itemAdapter.setOnItemClickListener { item ->
                                onItemClickListener?.let { it(item) }
                            }

                            rvFollowedNews.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                rvFollowedNews.visibility = View.GONE
            }
        }

    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
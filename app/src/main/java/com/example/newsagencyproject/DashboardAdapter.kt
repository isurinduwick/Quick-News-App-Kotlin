package com.example.newsagencyproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DashboardAdapter(private val newsList: List<DataClass>) :
    RecyclerView.Adapter<DashboardAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        val newsStatus: TextView = itemView.findViewById(R.id.newsStatus)
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]

        holder.newsTitle.text = newsItem.dataTitle ?: "No Title"

        // Truncate description in RecyclerView
        val truncatedDescription = newsItem.dataDesc?.take(20)?.plus("...") ?: "No Description"
        holder.newsDescription.text = truncatedDescription

        holder.newsStatus.text = "Status: ${newsItem.status ?: "Pending"}"

        // Set background color based on status
        when (newsItem.status) {
            "Approved" -> holder.newsStatus.setBackgroundResource(R.drawable.status_active)
            "Pending" -> holder.newsStatus.setBackgroundResource(R.drawable.status_pending)
            "Rejected" -> holder.newsStatus.setBackgroundResource(R.drawable.status_inactive)
            else -> holder.newsStatus.setBackgroundResource(R.drawable.status_pending)
        }

        // Load image using Glide
        if (!newsItem.dataImage.isNullOrEmpty()) {
            Log.d("ImageLoading", "Loading image from: ${newsItem.dataImage}")
            Glide.with(holder.itemView.context)
                .load(newsItem.dataImage)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.d)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.newsImage)
        } else {
            holder.newsImage.setImageResource(R.drawable.placeholder_image)
        }

        // Handle item click to show full news in NewsDetailsActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, NewsDetailsActivity::class.java).apply {
                putExtra("title", newsItem.dataTitle)
                putExtra("description", newsItem.dataDesc) // Full description
                putExtra("imageUrl", newsItem.dataImage) // Image URL
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = newsList.size
}

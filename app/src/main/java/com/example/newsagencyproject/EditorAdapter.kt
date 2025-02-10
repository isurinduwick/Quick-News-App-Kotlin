package com.example.newsagencyproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference

class EditorAdapter(
    private val context: Context,
    private val newsList: MutableList<Pair<String, DataClass>>, // Pair<UniqueKey, NewsData>
    private val database: DatabaseReference
) : RecyclerView.Adapter<EditorAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        val newsStatus: TextView = itemView.findViewById(R.id.newsStatus)
        val editorButtons: LinearLayout = itemView.findViewById(R.id.editorButtons)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (newsId, newsItem) = newsList[position]

        holder.newsTitle.text = newsItem.dataTitle
        holder.newsDescription.text = newsItem.dataDesc
        holder.newsStatus.text = newsItem.status

        // Load Image with Glide
        if (!newsItem.dataImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(newsItem.dataImage)
                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                .error(R.drawable.d) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache images
                .into(holder.newsImage)
        } else {
            holder.newsImage.setImageResource(R.drawable.placeholder_image)
        }

        // Set background color based on news status
        when (newsItem.status) {
            "Approved" -> holder.newsStatus.setBackgroundResource(R.drawable.status_active)
            "Pending" -> holder.newsStatus.setBackgroundResource(R.drawable.status_pending)
            "Rejected" -> holder.newsStatus.setBackgroundResource(R.drawable.status_inactive)
            else -> holder.newsStatus.setBackgroundResource(R.drawable.status_pending)
        }

        // Show Approve/Reject buttons only if the news is Pending
        if (newsItem.status == "Pending") {
            holder.editorButtons.visibility = View.VISIBLE
        } else {
            holder.editorButtons.visibility = View.GONE
        }

        holder.btnApprove.setOnClickListener {
            updateNewsStatus(newsId, "Approved", position)
        }

        holder.btnReject.setOnClickListener {
            updateNewsStatus(newsId, "Rejected", position)
        }
    }

    override fun getItemCount(): Int = newsList.size

    private fun updateNewsStatus(newsId: String, status: String, position: Int) {
        if (newsId.isNotEmpty()) {
            database.child(newsId).child("status").setValue(status)
                .addOnSuccessListener {
                    newsList[position] = newsList[position].copy(second = newsList[position].second.apply { this.status = status })
                    notifyItemChanged(position) // Update UI
                    Toast.makeText(context, "News marked as $status", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Invalid News ID", Toast.LENGTH_SHORT).show()
        }
    }
}

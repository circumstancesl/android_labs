package com.example.android_labs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagesAdapter(
    private val onLongClick: (Int) -> Unit,
    private val onClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private var items: List<ImageItem> = emptyList()

    fun updateItems(newItems: List<ImageItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val descriptionView: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView)
            .load(item.uri)
            .into(holder.imageView)

        holder.descriptionView.text = item.description
        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    override fun getItemCount() = items.size
}
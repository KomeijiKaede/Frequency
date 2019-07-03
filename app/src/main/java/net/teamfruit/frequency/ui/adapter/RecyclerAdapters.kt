package net.teamfruit.frequency.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.api.services.youtube.model.SearchResult
import net.teamfruit.frequency.R
import net.teamfruit.frequency.database.DBEntity

object RecyclerAdapters {
    interface OnClickListener<in T> {
        fun onClick(item: T)
        fun onLongClick(item: T)
    }

    private fun inflate(parent: ViewGroup) =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

    private fun <T> bind(holder: ViewHolder<T>, title: String, url: String, item: T) {
        holder.title.text = title
        Glide.with(holder.image).load(url).into(holder.image)
        holder.bind(item)
    }

    class ViewHolder<T>(itemView: View, private val listener: OnClickListener<T>): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.itemTextView)!!
        val image = itemView.findViewById<ImageView>(R.id.itemImageView)!!

        fun bind(item: T) {
            itemView.setOnClickListener {
                listener.onClick(item)
                return@setOnClickListener
            }
            itemView.setOnLongClickListener {
                listener.onLongClick(item)
                return@setOnLongClickListener false
            }
        }
    }

    class RecyclerAdapter(private var list: List<DBEntity>, private val listener: OnClickListener<DBEntity>): RecyclerView.Adapter<ViewHolder<DBEntity>>() {
        override fun getItemCount() = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(inflate(parent), listener)
        override fun onBindViewHolder(holder: ViewHolder<DBEntity>, position: Int) {
            val currentItem = list[position]
            bind(holder, currentItem.title, currentItem.thumbnail, currentItem)
        }
        fun addList(newList: List<DBEntity>) {
            this.list = newList
            notifyDataSetChanged()
        }
    }

    class BrowserAdapter(private var list: List<SearchResult>, private val listener: OnClickListener<SearchResult>): RecyclerView.Adapter<ViewHolder<SearchResult>>() {
        override fun getItemCount() = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(inflate(parent), listener)
        override fun onBindViewHolder(holder: ViewHolder<SearchResult>, position: Int) {
            val currentItem = list[position]
            bind(holder, currentItem.snippet.title, currentItem.snippet.thumbnails.high.url, currentItem)
        }
        fun addList(newList: List<SearchResult>) {
            this.list = newList
            notifyDataSetChanged()
        }
    }
}
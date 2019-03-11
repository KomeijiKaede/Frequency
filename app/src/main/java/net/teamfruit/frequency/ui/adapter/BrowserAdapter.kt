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

class BrowserAdapter(private var list: List<SearchResult>, private val listener: OnClickListener): RecyclerView.Adapter<BrowserAdapter.BrowserViewHolder>() {

    interface OnClickListener {
        fun onClick(res: SearchResult)
        fun onLongClick(res: SearchResult)
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowserViewHolder {
        return BrowserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: BrowserViewHolder, position: Int) {
        val currentItem = list[position]
        holder.title.text = currentItem.snippet.title
        Glide.with(holder.image).load(currentItem.snippet.thumbnails.high.url).into(holder.image)
        holder.bind(currentItem)
    }

    fun addList(newList: List<SearchResult>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class BrowserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.itemTextView)!!
        val image = itemView.findViewById<ImageView>(R.id.itemImageView)!!

        fun bind(item: SearchResult) {
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
}
package net.teamfruit.frequency.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.teamfruit.frequency.R
import net.teamfruit.frequency.database.DBEntity

class RecyclerAdapter(private var list: List<DBEntity>, private val listener: OnClickListener): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onClick(entity: DBEntity)
        fun onLongClick(entity: DBEntity)
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.title.text = currentItem.title
        holder.bind(currentItem)
    }

    fun addList(newList: List<DBEntity>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.itemTextView)!!

        fun bind(entity: DBEntity) {
            itemView.setOnClickListener {
                listener.onClick(entity)
                return@setOnClickListener
            }
            itemView.setOnLongClickListener {
                listener.onLongClick(entity)
                return@setOnLongClickListener false
            }
        }

    }
}
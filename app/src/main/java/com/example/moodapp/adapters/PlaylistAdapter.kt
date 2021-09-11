package com.example.moodapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moodapp.databinding.PlaylistItemBinding
import com.example.moodapp.models.userPlaylists.Item

/**
 * Adapter for recycler view
 * Uses diff util - calculates differences in 2 lists and allows only the different items to be updated and
 * happens in the background so that the main thread isn't blocked.
 */
class PlaylistAdapter: RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(val binding: PlaylistItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Item>(){

        //checks if 2 Items passed to this function are the same (looking at playlist id)
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        //compares the contents of old item and new item are the same
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Takes 2 lists and compares them and calculates the differences. It is asynchronous
     */
    val differ = AsyncListDiffer(this, differCallback)

    /**
     * Uses view binding so that views may be accessed in onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
       val binding = PlaylistItemBinding
           .inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
           )
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        var playlistUri: String
        val playlistItem = differ.currentList[position]
        holder.binding.apply {
            itemTitle.text = playlistItem.name
            playlistUri = playlistItem.uri
            holder.itemView.setOnClickListener{
                onItemClickListener?.let { it(playlistItem) }
            }
        }

    }

    /**
     * Onclick listener for each playlistitem
     */
    private var onItemClickListener: ((Item) -> Unit)? = null

    /**
     * Function which managers item clicks outside of PlaylistAdapter
     */
    fun setOnItemClickListener(listener: (Item) -> Unit){
        onItemClickListener = listener
    }




}
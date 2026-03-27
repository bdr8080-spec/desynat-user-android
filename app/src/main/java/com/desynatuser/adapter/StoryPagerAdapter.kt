package com.desynatuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.databinding.ItemStoryBinding

class StoryPagerAdapter(
    private val stories: List<String>
) : RecyclerView.Adapter<StoryPagerAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        val story = stories[position]

        Glide.with(holder.itemView.context)
            .load(story)
            .into(holder.binding.ivStory)
    }
}
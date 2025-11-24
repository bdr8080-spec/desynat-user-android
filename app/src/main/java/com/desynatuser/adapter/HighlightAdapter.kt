package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemHighlightsBinding

class HighlightAdapter(var context: Context, private val highlightList: List<String>) :
    RecyclerView.Adapter<HighlightAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHighlightsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemHighlightsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int = highlightList.size
}

package com.desynatuser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.databinding.ItemTopRatedBinding

class TopRatedAdapter(private val topRatedList: List<String>) :
    RecyclerView.Adapter<TopRatedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTopRatedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemTopRatedBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int = topRatedList.size
}

package com.desynatuser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCategoriesBinding

class CategoryAdapter(private val categoryList: List<String>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemCategoriesBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int = categoryList.size
}

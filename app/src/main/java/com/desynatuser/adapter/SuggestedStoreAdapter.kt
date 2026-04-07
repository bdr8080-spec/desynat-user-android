package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.databinding.ItemSuggestedStoreBinding

class SuggestedStoreAdapter(private val context: Context, private val categoryList: List<String>) :
    RecyclerView.Adapter<SuggestedStoreAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSuggestedStoreBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemSuggestedStoreBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int = categoryList.size
}

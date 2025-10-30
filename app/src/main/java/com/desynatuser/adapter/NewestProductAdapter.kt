package com.desynatuser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.databinding.ItemNewestProductBinding

class NewestProductAdapter(private val newestProductList: List<String>) :
    RecyclerView.Adapter<NewestProductAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNewestProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemNewestProductBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }

    override fun getItemCount(): Int = newestProductList.size
}

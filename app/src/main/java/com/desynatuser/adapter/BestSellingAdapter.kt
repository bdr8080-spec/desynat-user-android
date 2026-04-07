package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.databinding.ItemTopRatedBinding

class BestSellingAdapter(private val context: Context, private val bestSellingList: List<String>) :
    RecyclerView.Adapter<BestSellingAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = bestSellingList.size
}

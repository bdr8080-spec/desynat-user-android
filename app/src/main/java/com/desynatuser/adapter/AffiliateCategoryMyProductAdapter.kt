package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemTabBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse

class AffiliateCategoryMyProductAdapter(var context: Context, private val categoryList: List<GetCategoryResponse.Data>, var categoryClick:CategoryClick) :
    RecyclerView.Adapter<AffiliateCategoryMyProductAdapter.ViewHolder>() {
        var selectedPos = 0
        interface CategoryClick{
            fun onCategoryClick(position: Int)
        }

    inner class ViewHolder(val binding: ItemTabBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemTabBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvItem.text = categoryList[position].name
            relSelection.isVisible = selectedPos==position
        }

        holder.itemView.setOnClickListener {
            selectedPos = position
            categoryClick.onCategoryClick(position)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int = categoryList.size
}

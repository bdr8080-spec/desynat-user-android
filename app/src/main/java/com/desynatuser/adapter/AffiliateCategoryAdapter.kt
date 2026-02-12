package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse

class AffiliateCategoryAdapter(var context: Context, private val categoryList: List<GetCategoryResponse.Data>, var categoryClick:CategoryClick) :
    RecyclerView.Adapter<AffiliateCategoryAdapter.ViewHolder>() {
        var selectedPos = -1
        interface CategoryClick{
            fun onCategoryClick(position: Int)
        }

    inner class ViewHolder(val binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemCategoriesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvCategory.text = categoryList[position].name

            ivCategory.load(categoryList[position].image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            if (selectedPos==position){
                rlSelected.isVisible = true
            }
            else{
                rlSelected.isVisible = false
            }
        }

        holder.itemView.setOnClickListener {
            categoryClick.onCategoryClick(position)
        }

    }

    override fun getItemCount(): Int = categoryList.size
}

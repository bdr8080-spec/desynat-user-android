package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse

class CategoryAdapter(var context: Context, private val categoryList: List<GetSubCategoryResponse.Data>, var categoryClick:CategoryClick) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        interface CategoryClick{
            fun onCategoryClick(position: Int)
        }

    inner class ViewHolder(val binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemCategoriesBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvCategory.text = categoryList[position].nameEnglish

            ivCategory.load(categoryList[position].image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            categoryClick.onCategoryClick(position)
        }

    }

    override fun getItemCount(): Int = categoryList.size
}

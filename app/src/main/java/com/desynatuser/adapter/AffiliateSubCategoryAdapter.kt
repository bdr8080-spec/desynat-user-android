package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemSubCategoryBinding
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse

class AffiliateSubCategoryAdapter(var context: Context, private val subCategoryList: List<GetSubCategoryResponse.Data>, var subCategoryClick:SubCategoryClick) :
    RecyclerView.Adapter<AffiliateSubCategoryAdapter.ViewHolder>() {

        interface SubCategoryClick{
            fun onSubCategoryClick(position: Int)
        }

    inner class ViewHolder(val binding: ItemSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemSubCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvBrandName.text = subCategoryList[position].nameEnglish

            ivSubCategory.load(subCategoryList[position].image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            subCategoryClick.onSubCategoryClick(position)
        }

    }

    override fun getItemCount(): Int = subCategoryList.size
}

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemSubCategoryBinding
import com.desynatuser.databinding.ItemSubCategoryTabBinding
import com.desynatuser.databinding.ItemTabBinding
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse

class AffiliateSubCategoryMyProductAdapter(var context: Context, private val subCategoryList: List<GetSubCategoryResponse.Data>, var subCategoryClick:SubCategoryClick) :
    RecyclerView.Adapter<AffiliateSubCategoryMyProductAdapter.ViewHolder>() {

        interface SubCategoryClick{
            fun onSubCategoryClick(position: Int)
        }

    inner class ViewHolder(val binding: ItemSubCategoryTabBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemSubCategoryTabBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (position==0){
                ivAll.isVisible = false
                tvName.isVisible = false
                ivSubCategory.isVisible = true
            }
            else{
                ivAll.isVisible = false
                tvName.isVisible = true
                ivSubCategory.isVisible = true
                tvName.text = subCategoryList[position].nameEnglish
                ivSubCategory.load(subCategoryList[position].image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }

        }

        holder.itemView.setOnClickListener {
            subCategoryClick.onSubCategoryClick(position)
        }

    }

    override fun getItemCount(): Int = subCategoryList.size
}

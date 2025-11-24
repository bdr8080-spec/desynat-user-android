package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoryBinding
import com.desynatuser.ui.home.response.GetCategoryResponse

class CatalogAdapter(var context:Context, private var catalogList: ArrayList<GetCategoryResponse.Data>, var catalogClick:CatalogClick) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface CatalogClick{
        fun onCatalogClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return catalogList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var catalogData = catalogList[position]

        holder.binding.apply {
            tvCategoryName.text = catalogData.name
            tvDescription.text = catalogData.description
            ivCategory.load(catalogData.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            relCategory.backgroundTintList = ColorStateList.valueOf(Color.parseColor(catalogData.colorCode))

        }

        holder.itemView.setOnClickListener {
            catalogClick.onCatalogClick(position)

        }
    }

}

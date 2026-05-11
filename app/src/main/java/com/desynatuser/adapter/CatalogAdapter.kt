package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoryBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.utils.Constants

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

            val params = holder.itemView.layoutParams
            params.height = itemHeight
            holder.itemView.layoutParams = params


            if (position % 2 == 0) {
                clRight.isVisible = true
                clLeft.isVisible = false
            } else {
                clRight.isVisible = false
                clLeft.isVisible = true
            }

            tvCategoryName.text = if (Constants.language=="ar") catalogData.nameAr else catalogData.name
            tvDescription.text = catalogData.description
            ivCategory.load(catalogData.image)

            relCategory.backgroundTintList = ColorStateList.valueOf(Color.parseColor(catalogData.colorCode))

            tvCategoryName1.text = if (Constants.language=="ar") catalogData.nameAr else catalogData.name
            tvDescription1.text = catalogData.description
            ivCategory1.load(catalogData.image)

            relCategory1.backgroundTintList = ColorStateList.valueOf(Color.parseColor(catalogData.colorCode))

        }

        holder.itemView.setOnClickListener {
            catalogClick.onCatalogClick(position)

        }
    }

    private val itemHeight: Int by lazy {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.heightPixels / 4
    }

}

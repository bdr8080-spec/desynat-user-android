package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemSubCategoryBinding
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
class AffiliateSubCategoryAdapter(var context:Context, private var subCategoryList: ArrayList<GetSubCategoryResponse.Data>, var subCategoryClick:SubCategoryClick) : RecyclerView.Adapter<AffiliateSubCategoryAdapter.ViewHolder>() {

    private var filteredList: ArrayList<GetSubCategoryResponse.Data> = ArrayList(subCategoryList)

    inner class ViewHolder(val binding: ItemSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface SubCategoryClick{
        fun onSubCategoryClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var subCategoryData = filteredList[position]

        holder.binding.apply {
            tvBrandName.text = subCategoryData.nameEnglish
            ivSubCategory.load(subCategoryData.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            subCategoryClick.onSubCategoryClick(position,1)

        }
    }

    fun setData(newList: ArrayList<GetSubCategoryResponse.Data>) {
        subCategoryList = newList
        filteredList = ArrayList(subCategoryList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(subCategoryList)
        } else {
            val result = subCategoryList.filter {
                it.nameEnglish?.contains(query, ignoreCase = true) == true
            }
            ArrayList(result)
        }
        if(filteredList.isEmpty()) {
            subCategoryClick.onSubCategoryClick(-1,2)
        }
        else{
            subCategoryClick.onSubCategoryClick(0,2)
        }
        notifyDataSetChanged()
    }

}


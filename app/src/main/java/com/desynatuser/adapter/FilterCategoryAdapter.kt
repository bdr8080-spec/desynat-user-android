package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.des.adapter.SizeAdapter.OnClick
import com.desynatuser.R
import com.desynatuser.databinding.ItemBrandBinding
import com.desynatuser.databinding.ItemColorBinding
import com.desynatuser.databinding.ItemColorFilterBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse

class FilterCategoryAdapter(var context:Context, var categoryList: ArrayList<GetCategoryResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    var selectedPos = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBrandBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvBrand.text = categoryList[position].name

            if (selectedPos==position){
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            selectedPos = position
            onClick.onClickItem(position)
            notifyDataSetChanged()
        }

    }

    fun setSelectedCategoryId(id: Int?) {
        selectedPos = categoryList.indexOfFirst { it.id == id }
        notifyDataSetChanged()
    }

}

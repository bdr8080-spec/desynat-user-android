package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemBrandBinding
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.utils.Constants

class FilterBrandAdapter(var context:Context, var brandList: ArrayList<GetAllBrandListResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterBrandAdapter.ViewHolder>() {

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
        return brandList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvBrand.text = if (Constants.language=="ar") brandList[position].nameArabic else brandList[position].nameEnglish

            if (brandList[position].isSelected){
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }

        }


        holder.itemView.setOnClickListener {
            brandList[position].isSelected = !brandList[position].isSelected
                        notifyDataSetChanged()
        }

    }


}

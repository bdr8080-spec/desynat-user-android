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
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse

class FilterBrandAdapter(var context:Context, var brandList: ArrayList<String>, var onClick: OnClick) : RecyclerView.Adapter<FilterBrandAdapter.ViewHolder>() {

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
        return brandList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

           /* tvColorName.text = colorList[position].name
            ivRectangleColor.setColorFilter(Color.parseColor(colorList[position].hex_code))*/

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


}

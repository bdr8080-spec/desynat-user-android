package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemColorBinding
import com.desynatuser.ui.home.store.response.Product

class ProductColorAdapter(var context:Context, var productColorList: ArrayList<Product>, var onClick: OnClick) : RecyclerView.Adapter<ProductColorAdapter.ViewHolder>() {

    var selectedPos = 0
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productColorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            ivRectangleColor.setColorFilter(Color.parseColor(productColorList[position].color.hex_code))

            if (selectedPos==position){
                ivSelected.isVisible = true
            }
            else{
                ivSelected.isInvisible = true
            }
        }


        holder.itemView.setOnClickListener {
            selectedPos = position
            onClick.onClickItem(position)
            notifyDataSetChanged()
        }

    }


}

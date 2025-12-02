package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemSizeBinding
import com.desynatuser.ui.home.store.response.Product

class SizeAdapter(var context:Context, var productSizeList: ArrayList<Product.ProductVariation>, var onClick: OnClick) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    var selectedPos = 0
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSizeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productSizeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvSize.text = productSizeList[position].size.name

            if (selectedPos==position){
                ivColorSelected.isVisible = true
                ivColor.isVisible = false
                tvSize.setTextColor(Color.parseColor("#000000"))
            }
            else{
                ivColorSelected.isVisible = false
                ivColor.isVisible = true
                tvSize.setTextColor(Color.parseColor("#E6E6E6"))
            }
        }

        holder.itemView.setOnClickListener {
            selectedPos = position
            onClick.onClickItem(position)
            notifyDataSetChanged()
        }

    }


    fun updateData(pos:Int){
        selectedPos = pos
        notifyDataSetChanged()
    }



}

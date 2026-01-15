package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemFullScreenImageBinding

class HomeFullScreenBannerAdapter(var context:Context, var productList: ArrayList<String>, var click:OnClick) : RecyclerView.Adapter<HomeFullScreenBannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemFullScreenImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFullScreenImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
            ivProduct.load(productData) {
                crossfade(true)
                placeholder(R.color.white)
                error(R.color.white)
            }

            ivProduct.setOnClickListener {
                click.onClick(position)
            }

        }
    }


}

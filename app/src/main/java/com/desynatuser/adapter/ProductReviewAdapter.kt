package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.databinding.ItemReviewBinding

class ProductReviewAdapter(var context:Context, var reviewListList: ArrayList<String>,var click: Click) : RecyclerView.Adapter<ProductReviewAdapter.ViewHolder>() {
    interface Click{
        fun onClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reviewListList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            ivImage.load(reviewListList[position])
        }

        holder.binding.ivImage.setOnClickListener {
            click.onClick(position)
        }
    }


}

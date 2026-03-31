package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse

class ReelAdapter(var context: Context, private val reelList: List<GetSellerStoryResponse.Data>,var onClick: OnClick) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    interface OnClick{
        fun onItemClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val reelData = reelList[position]

            Glide.with(context).load(reelData?.file).placeholder(
                R.drawable.ic_placeholder).into(ivProfile)

            root.setOnClickListener {
                onClick.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = reelList.size
}

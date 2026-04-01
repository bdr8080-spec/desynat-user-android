package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse

class ReelAdapter(var context: Context, private val reelList: List<GetSellerStoryResponse.Data>, var from:Int,var onClick: OnClick) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val reelData = reelList[position]

            Glide.with(context).load(reelData?.file).placeholder(
                R.drawable.ic_placeholder).into(ivProfile)

            if (from!=1) {
                if (reelData.wishlist == 1) {
                    ivLike.setImageResource(R.drawable.ic_like_filled)
                } else {
                    ivLike.setImageResource(R.drawable.ic_like)
                }
            }
            else{
                ivLike.setImageResource(R.drawable.ic_like_filled)
            }

            root.setOnClickListener {
                onClick.onItemClick(position,1)
            }

            ivLike.setOnClickListener {
                onClick.onItemClick(position,2)
            }

            ivShare.setOnClickListener {
                onClick.onItemClick(position,3)
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

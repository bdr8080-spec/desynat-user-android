package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemHighlightsBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse

class HighlightAdapter(var context: Context, private val highlightList: List<GetSellerStoryResponse.Data> ,var onClick:OnClick) :
    RecyclerView.Adapter<HighlightAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemHighlightsBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding) {
            val highlightData = highlightList[position]
            tvName.text = highlightData?.title
            Glide.with(context).load(highlightData?.file).placeholder(
                R.drawable.ic_placeholder).into(ivHighlight)

            root.setOnClickListener {
                onClick.onItemClick(position)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemHighlightsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = highlightList.size
}

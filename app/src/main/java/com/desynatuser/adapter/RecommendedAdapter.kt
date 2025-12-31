package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemRecommendedBinding
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse

class RecommendedAdapter(var context:Context, var list: ArrayList<GetRecentSearchListResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecommendedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvName.text = list[position].name
        }

        holder.itemView.setOnClickListener {

            onClick.onClickItem(position)
        }

    }


}

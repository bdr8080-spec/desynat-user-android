package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemLatestSearchBinding
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse

class LatestSearchAdapter(var context:Context, var list: ArrayList<GetRecentSearchListResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<LatestSearchAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemLatestSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLatestSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvLatest.text = list[position].name
        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}

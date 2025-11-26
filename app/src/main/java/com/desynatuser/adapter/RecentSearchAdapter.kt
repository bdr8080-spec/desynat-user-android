package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemRecentSearchBinding
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse

class RecentSearchAdapter(var context:Context, var list: ArrayList<String>, var onClick:ClickOnItem) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecentSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}

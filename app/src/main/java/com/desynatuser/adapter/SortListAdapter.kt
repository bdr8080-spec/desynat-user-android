package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemSortBinding
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.utils.Constants

class SortListAdapter(var context:Context, var sortList: ArrayList<GetStoreSortListResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<SortListAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemSortBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSortBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return sortList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
          tvName.text = if (Constants.language=="ar") sortList[position].nameAr else sortList[position].name

            if (sortList[position].isSelected){
                ivSort.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivSort.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            sortList[position].isSelected = !sortList[position].isSelected
            onClick.onClickItem(position)
            notifyDataSetChanged()
        }

    }


}

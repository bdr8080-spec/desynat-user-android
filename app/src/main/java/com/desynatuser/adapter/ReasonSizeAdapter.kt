package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemReasonBinding
import com.desynatuser.ui.home.store.response.GetSizeListResponse

class ReasonSizeAdapter(var context:Context, var sizeList: ArrayList<GetSizeListResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<ReasonSizeAdapter.ViewHolder>() {
    var isSelected = -1

    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemReasonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvReason.text = sizeList[position].name

            if (isSelected==position){
                ivImage.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            isSelected = position
            notifyDataSetChanged()

            onClick.onClickItem(position)
        }

    }


}

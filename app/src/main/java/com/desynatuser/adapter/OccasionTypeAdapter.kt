package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemGiftCardBinding
import com.desynatuser.databinding.ItemOccasionBinding
import com.desynatuser.ui.home.response.GetAllGiftCardResponse

class OccasionTypeAdapter(var context:Context, var occasionList: ArrayList<String>, var click:OnClick) : RecyclerView.Adapter<OccasionTypeAdapter.ViewHolder>() {

    var selected = -1

    inner class ViewHolder(val binding: ItemOccasionBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClick{
        fun clickOnItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOccasionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return occasionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            tvOccasion.text = occasionList[position]

            if (selected==position){
                tvOccasion.setTextColor(context.getColor(R.color.white))
                tvOccasion.setBackgroundColor(context.getColor(R.color.brown))
            }
            else{
                tvOccasion.setTextColor(context.getColor(R.color.brown))
                tvOccasion.setBackgroundColor(context.getColor(R.color.white))
            }
        }

        holder.itemView.setOnClickListener {
            click.clickOnItem(position)
            selected = position
            notifyDataSetChanged()

        }
    }

}

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemOccasionBinding
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse

class OccasionTypeAdapter(var context:Context, var occasionList: ArrayList<GetGiftCardOccasionTypeResponse.Data>, var click:OnClick) : RecyclerView.Adapter<OccasionTypeAdapter.ViewHolder>() {

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
            tvOccasion.text = occasionList[position].nameEnglish

            if (selected==position){
                tvOccasion.setTextColor(context.getColor(R.color.white))
                llOccasion.setBackgroundResource(R.drawable.btn_gradient)
            }
            else{
                tvOccasion.setTextColor(context.getColor(R.color.brown))
                llOccasion.setBackgroundResource(R.drawable.button_stroke)
            }
        }

        holder.itemView.setOnClickListener {
            click.clickOnItem(position)
            selected = position
            notifyDataSetChanged()

        }
    }

    fun updateSelectedPos(pos:Int){
        selected = pos
        notifyDataSetChanged()
    }

}

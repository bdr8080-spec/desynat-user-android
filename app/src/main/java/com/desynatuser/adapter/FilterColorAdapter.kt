package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.des.adapter.SizeAdapter.OnClick
import com.desynatuser.R
import com.desynatuser.databinding.ItemColorBinding
import com.desynatuser.databinding.ItemColorFilterBinding
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse

class FilterColorAdapter(var context:Context, var colorList: ArrayList<GetAllColorListResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterColorAdapter.ViewHolder>() {

    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemColorFilterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemColorFilterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvColorName.text = colorList[position].name
            ivRectangleColor.setColorFilter(Color.parseColor(colorList[position].hexCode))

            if (colorList[position].isSelected){
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }

        }


        holder.itemView.setOnClickListener {
            colorList[position].isSelected = !colorList[position].isSelected
            notifyDataSetChanged()
            onClick.onClickItem(position)
        }

    }

}

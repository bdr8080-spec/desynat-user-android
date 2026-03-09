package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemChangeCountryBinding
import com.desynatuser.ui.auth.response.GetCountryResponse

class ChangeCountryAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<ChangeCountryAdapter.ViewHolder>() {

    var selectedPos = 0
    inner class ViewHolder(val binding: ItemChangeCountryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val countryData = countryList[position]
            tvCountry.text = countryData.name
            Glide.with(context).load(countryData.flag).into(ivFlag)
            if (selectedPos==position){
                ivRadio.setImageResource(R.drawable.ic_radio_selected)
                clMain.setBackgroundResource(R.drawable.notification_read_background)
            }
            else{
                ivRadio.setImageResource(R.drawable.ic_radio_unselected)
                clMain.setBackgroundResource(R.drawable.notification_unread_background)
            }

            clMain.setOnClickListener {
                selectedPos = position
                clickOnItem.onClickItem(position)
                notifyDataSetChanged()
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChangeCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemCurrencyBinding

class CurrencyAdapter(var context:Context, var currencyList: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var selectedPos = 0
    inner class ViewHolder(val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val currencyData = currencyList[position]
            tvCurrency.text = currencyData
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
            ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

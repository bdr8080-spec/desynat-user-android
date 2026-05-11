package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemCountryBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.utils.Constants

class CountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>,var isDial:Boolean=false, var onClick:ClickOnItem) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (isDial){
                tvCountry.text = "${countryList[position].flag1}  ${if(Constants.language=="ar") countryList[position]?.nameAr?:countryList[position]?.name else countryList[position].name } (${countryList[position].dialCode})"
            }
            else{
                tvCountry.text = "${countryList[position].flag1}  ${if (Constants.language == "ar") countryList[position]?.nameAr ?: countryList[position]?.name else countryList[position].name}"
            }

            ivFlag.isVisible = false
        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}

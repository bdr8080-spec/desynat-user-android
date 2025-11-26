package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoryBinding
import com.desynatuser.databinding.ItemSavedAddressBinding
import com.desynatuser.ui.home.response.GetAddressResponse
import com.desynatuser.ui.home.response.GetCategoryResponse

class SavedAddressAdapter(var context:Context, private var addressList: ArrayList<GetAddressResponse.Data>, var addressClick:AddressClick) : RecyclerView.Adapter<SavedAddressAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSavedAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface AddressClick{
        fun onAddressClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSavedAddressBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var addressData = addressList[position]

        holder.binding.apply {
            // 0 for office 1 for home
            if (addressData.saveAddressAs==0){
                tvAddressType.text = "Office"
                tvAddressType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_office,  0,0,0)
            }
            else{
                tvAddressType.text = "Home"
                tvAddressType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home,  0,0,0)
            }

            tvName.text = addressData.name
            tvAddress.text = addressData.address

            ivDelete.setOnClickListener {
                addressClick.onAddressClick(position,2)
            }

            ivEdit.setOnClickListener {
                addressClick.onAddressClick(position,1)
            }

            llAddressType.setOnClickListener {
                addressClick.onAddressClick(position,3)
            }

            if (addressData.isSelected){
                ivRadio.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivRadio.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

    }

}

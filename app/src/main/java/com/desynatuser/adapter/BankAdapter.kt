package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemBankBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse

class BankAdapter(var context: Context, private val bankList: List<GetAffiliateUserProfileResponse.Data.BankDetails>, var click:OnClick) :
    RecyclerView.Adapter<BankAdapter.ViewHolder>() {

        interface OnClick{
            fun onClick(position: Int,type: Int)
        }

    inner class ViewHolder(val binding: ItemBankBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int) = with(binding) {

                view.isVisible = bankList.size-1 != position
                val bankData = bankList[position]

                tvAccountNumber.text = bankData.accountNo

                ivEdit.setOnClickListener{
                    click.onClick(position,1)
                }

                ivDelete.setOnClickListener{
                    click.onClick(position,2)
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemBankBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    override fun getItemCount(): Int = bankList.size
}

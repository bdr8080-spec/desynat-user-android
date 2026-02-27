package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemBankBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse

class BankAdapter(var context: Context, private val bankList: List<GetAffiliateUserProfileResponse.Data.BankDetails>, var isShow: Boolean=true, var click:OnClick) :
    RecyclerView.Adapter<BankAdapter.ViewHolder>() {
        var selectedPos = 0
        interface OnClick{
            fun onClick(position: Int,type: Int)
        }

    inner class ViewHolder(val binding: ItemBankBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int) = with(binding) {

                if (isShow){
                    ivEdit.isVisible = true
                    ivDelete.isVisible = true
                }
                else{
                    ivEdit.isVisible = false
                    ivDelete.isVisible = false
                }

                if (selectedPos==position){
                    ivRadio.setImageResource(R.drawable.ic_radio_selected)
                }
                else{
                    ivRadio.setImageResource(R.drawable.ic_radio_unselected)
                }

                view.isVisible = bankList.size-1 != position
                val bankData = bankList[position]

                tvAccountNumber.text = bankData.accountNo

                ivEdit.setOnClickListener{
                    click.onClick(position,1)
                }

                ivDelete.setOnClickListener{
                    click.onClick(position,2)
                }

                clAccount.setOnClickListener {
                    if (selectedPos!=position){
                        selectedPos = position
                        click.onClick(position,3)
                        notifyDataSetChanged()
                    }
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

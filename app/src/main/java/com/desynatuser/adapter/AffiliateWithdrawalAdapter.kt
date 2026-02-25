package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemWithdrawalBinding
import com.desynatuser.utils.CommonMethod

class AffiliateWithdrawalAdapter(var context:Context, var withdrawalList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<AffiliateWithdrawalAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemWithdrawalBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val withdrawalData = withdrawalList[position]

        /*    tvRequestIdValue.text = withdrawalData?.sellerDetails
            tvAccountNumber.text = withdrawalData?.firstName
            tvTransaction.text = withdrawalData?.firstName
            tvTotalAmount.text = "${CommonMethod.formatPrice(110.00)} KD"
            tvCreditOn.text = CommonMethod.customizeChangeDateFormat(withdrawalData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a" )
            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(withdrawalData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a" )

                when(withdrawalData?.adminVerify){
                    0->{
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                    }
                    1->{
                        tvStatusValue.text = "Completed"
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                    }
                }*/
            }
        }

    interface OnClick{
        fun click(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWithdrawalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return withdrawalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

    fun setData(newList: ArrayList<String>) {
        withdrawalList = newList
        notifyDataSetChanged()
    }
}

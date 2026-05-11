package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemWithdrawalBinding
import com.desynatuser.ui.affiliateUser.home.payment.response.GetWithdrawalRequestResponse
import com.desynatuser.utils.CommonMethod

class AffiliateWithdrawalAdapter(var context:Context, var withdrawalList: ArrayList<GetWithdrawalRequestResponse.Data.WithdrawRequests.WithdrawRequest>, var onClick:OnClick) : RecyclerView.Adapter<AffiliateWithdrawalAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemWithdrawalBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val withdrawalData = withdrawalList[position]

            tvRequestIdValue.text = "#${withdrawalData?.requestId}"
            tvAccountNumber.text = withdrawalData?.bankDetails?.accountNo
            tvTotalAmountValue.text = "${CommonMethod.formatPrice(withdrawalData?.amount?.toDoubleOrNull()?:0.0)} KD"
            tvCreditOn.text = CommonMethod.customizeChangeDateFormat(withdrawalData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a" )
            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(withdrawalData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a" )

                when(withdrawalData?.adminVerify){
                    0->{
                        tvStatusValue.text = context.getString(R.string.pending)
                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                    }
                    1->{
                      //  tvStatusValue.text = "Completed"
                        tvStatusValue.text = context.getString(R.string.credit)
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        tvCredited.isVisible = true
                        tvTransaction.isVisible = true
                        tvCreditOn.isVisible = true
                        tvTransactionValue.isVisible = true
                        tvTransactionValue.text = "#${withdrawalData?.transactionId?:withdrawalData?.requestId}"

                    }
                    2->{
                        tvStatusValue.text = context.getString(R.string.rejected)
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvReasonValue.text = withdrawalData?.rejectReason
                    }
                }
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

    fun setData(newList: ArrayList<GetWithdrawalRequestResponse.Data.WithdrawRequests.WithdrawRequest>) {
        withdrawalList = newList
        notifyDataSetChanged()
    }
}

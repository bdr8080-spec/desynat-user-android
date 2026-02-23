package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemPaymentBinding
import com.desynatuser.utils.CommonMethod

class AffiliatePaymentAdapter(var context:Context, var paymentList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<AffiliatePaymentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]

            /*tvPurchasedByValue.text = paymentData?.sellerDetails?.firstName+" "+ paymentData?.sellerDetails?.lastName
            tvTransactionIdValue.text = paymentData?.firstName
            tvProductName.text = paymentData?.firstName
            tvCategoryValue.text = paymentData?.firstName
            tvQuantityValue.text = paymentData?.firstName
            tvMyEarnings.text = "${CommonMethod.formatPrice(110.00)} KD"
            tvTotalAmount.text = "${CommonMethod.formatPrice(110.00)} KD"
            tvReasonValue.text = paymentData?.rejectReason
            tvPurchaseOnValue.text = CommonMethod.customizeChangeDateFormat(paymentData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a" )

                when(paymentData?.adminVerify){
                    0->{
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                    1->{
                        tvStatusValue.text = "Completed"
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                    2->{
                        tvStatusValue.text = "Cancelled"
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                    }
                }*/
            }
        }

    interface OnClick{
        fun click(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

    fun setData(newList: ArrayList<String>) {
        paymentList = newList
        notifyDataSetChanged()
    }
}

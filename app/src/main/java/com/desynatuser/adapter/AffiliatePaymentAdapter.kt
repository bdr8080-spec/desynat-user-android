package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemPaymentBinding
import com.desynatuser.ui.affiliateUser.home.payment.response.GetAllAffiliateOrderResponse
import com.desynatuser.utils.CommonMethod

class AffiliatePaymentAdapter(var context:Context, var paymentList: ArrayList<GetAllAffiliateOrderResponse.Data.Orders.Data>, var onClick:OnClick) : RecyclerView.Adapter<AffiliatePaymentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]

            tvPurchasedByValue.text = paymentData?.userDetails?.fullName
            tvTransactionIdValue.text = "#${paymentData?.order?.transactionId?:paymentData?.id}"
            tvProductName.text = paymentData?.productDetails?.productNameEn
            tvCategoryValue.text = paymentData?.productDetails?.category?.name
            tvQuantityValue.text = paymentData?.quantity.toString()
            tvMyEarnings.text = "${CommonMethod.formatCurrencyAmount(context,paymentData?.affiliateCommission?.toDoubleOrNull()?:0.0)}"
            tvTotalAmount.text = "${CommonMethod.formatCurrencyAmount(context,paymentData?.amount?.toDoubleOrNull()?:0.0)}"
            tvReasonValue.text = paymentData?.cancelReason
            tvPurchaseOnValue.text = CommonMethod.customizeChangeDateFormat(paymentData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a" )

            if (paymentData?.paymentStatus=="2"||paymentData?.paymentStatus=="3"){
                tvStatusValue.text = context.getString(R.string.cancelled)
                tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                tvReason.isVisible = true
                tvReasonValue.isVisible = true
            }
            else {
                when (paymentData?.deliveryStatus) {
//                    "0" -> {
//                        tvStatusValue.text = "Pending"
//                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
//                        tvReason.isVisible = false
//                        tvReasonValue.isVisible = false
//                    }

                    "3"->{
                        tvStatusValue.text = context.getString(R.string.completed)
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }

/*                    "3" -> {
                        if (paymentData?.rEType == "0" && paymentData?.pOrderdetailsId == "0" && paymentData?.rEApprove == "0" && paymentData?.returnType == "0") {
                            tvStatusValue.text = "Completed"
                            tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }
                        else if (paymentData?.rEType == "0" && paymentData?.pOrderdetailsId == "0" && paymentData?.rEApprove == "1" && paymentData?.returnType == "2") {

//                            if (paymentData?.exchangeOrderDetails?.deliveryStatus=="7"){
//                                tvStatusValue.text = "Ongoing"
//                                tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
//                            }
//                            else if (paymentData?.exchangeOrderDetails?.deliveryStatus=="3"){
//                                tvStatusValue.text = "Exchanged"
//                                tvStatusValue.setTextColor(Color.parseColor("#16B364"))
//                            }
//                            else{
//                                tvStatusValue.text = "Exchange Req.."
//                                tvStatusValue.setTextColor(Color.parseColor("#FF5F1B"))
//                            }

                            tvStatusValue.text = "Pending"
                            tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }

                        else if (paymentData?.rEType == "0" && paymentData?.pOrderdetailsId == "0" && paymentData?.rEApprove == "0" && paymentData?.returnType == "2") {
//                            tvStatusValue.text = "Exchange Req.."
//                            tvStatusValue.setTextColor(Color.parseColor("#FF5F1B"))

                            tvStatusValue.text = "Pending"
                            tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }

                        else if (paymentData?.rEType=="0"&&paymentData?.pOrderdetailsId=="0"&&paymentData?.rEApprove=="0"&&paymentData?.returnType=="1"){
//                            tvStatusValue.text = "Return Req.."
//                            tvStatusValue.setTextColor(Color.parseColor("#FF5F1B"))
                            tvStatusValue.text = "Pending"
                            tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }
                        else if (paymentData?.rEType=="0"&&paymentData?.pOrderdetailsId=="0"&&paymentData?.rEApprove=="1"&&paymentData?.returnType=="1") {
//                            tvStatusValue.text = "Returned"
//                            tvStatusValue.setTextColor(Color.parseColor("#FF5F1B"))
                            tvStatusValue.text = "Pending"
                            tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }
                        else{
                            tvStatusValue.text = "Completed"
                            tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                            tvReason.isVisible = false
                            tvReasonValue.isVisible = false
                        }

                        }
                    "5"->{
//                        tvStatusValue.text = "Return Req.."
//                        tvStatusValue.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }*/

                    "4" -> {
                        tvStatusValue.text = context.getString(R.string.cancelled)
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                    }
                    else ->{
                        tvStatusValue.text = context.getString(R.string.pending)
                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }

//                    else -> {
//                        tvStatusValue.text = "Ongoing"
//                        tvStatusValue.setTextColor(Color.parseColor("#FE7D49"))
//                        tvReason.isVisible = false
//                        tvReasonValue.isVisible = false
//                    }
                }
            }
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

    fun setData(newList: ArrayList<GetAllAffiliateOrderResponse.Data.Orders.Data>) {
        paymentList = newList
        notifyDataSetChanged()
    }
}

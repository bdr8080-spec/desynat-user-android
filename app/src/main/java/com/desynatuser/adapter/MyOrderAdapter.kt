package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemOrderBinding
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants

class MyOrderAdapter(var context:Context, private var orderList: ArrayList<GetAllOrderResponse.Data>, var orderClick:OrderClick) : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OrderClick{
        fun onOrderClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var orderData = orderList[position]

        holder.binding.apply {

            tvOrderId.text = "${context.getString(R.string.order_id)}: #${orderData?.id}"
            tvTitle.text = if (Constants.language=="ar") orderData?.productDetails?.productNameAr else orderData?.productDetails?.productNameEn

         //   tvPrice.text = "${CommonMethod.formatPrice(orderData?.amount?.toDoubleOrNull()?:0.0)} KD"
            tvPrice.text = "${CommonMethod.formatCurrencyAmount(context,orderData?.amount?.toDoubleOrNull()?:0.0)}"

            ivProduct.load(orderData?.variationDetails?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        //    tvPlacedDate.text = CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "hh a  | dd MM yy")
            tvPlacedDate.text = CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "dd/MM/yy '${context.getString(R.string.at)}' hh:mma")
            tvSizeValue.text =  if(Constants.language=="ar") orderData?.variationDetails?.size?.name.toString() else orderData?.variationDetails?.size?.name.toString()
            orderData?.variationDetails?.color
                ?.hexCode?.let { ivColor.setColorFilter(Color.parseColor(it)) }
            tvQuantity.text = orderData?.quantity.toString()

            if (orderData?.paymentStatus=="2"||orderData?.paymentStatus=="3"){
                tvStatus.text = context.getString(R.string.cancelled)
                tvStatus.setTextColor(Color.parseColor("#E70000"))
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F4DDDD"))
                btn.setTextColor(Color.parseColor("#E70000"))
                btn.text = "${context.getString(R.string.cancelled_date)} : ${CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd MMM yy")}"
            }
            else {
                when (orderData?.deliveryStatus) {//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                    "0" -> {
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                        btn.setTextColor(Color.parseColor("#FF5F1B"))
                        btn.text = "${context.getString(R.string.pickup_date)} : 21 MAY 26"
                    }
                    "1" -> {

                        if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.order?.assignStatus=="0"){
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.order?.assignStatus=="1"){
                            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.pickup_date)} : 21 MAY 26"
                            tvStatus.text = "${context.getString(R.string.ongoing)}"
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else{
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                        if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "0") {
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))

                            ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.pickup_date)} : 20 MAY 26"

                        } else if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "1") {
                            tvStatus.text = context.getString(R.string.out_for_picked)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.pickup_date)} : 20 MAY 26"

                        } else {
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1AB364"))
                            btn.setTextColor(Color.parseColor("#17854C"))
                            btn.text = "${context.getString(R.string.expected_delivery_date)} : 3 ${context.getString(R.string.to)} 5 ${
                                context.getString(
                                    R.string.working_days
                                )
                            }"
                        }
                    }

                    "2" -> {
                        btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1AB364"))
                        btn.setTextColor(Color.parseColor("#17854C"))
                        btn.text = "${context.getString(R.string.expected_delivery_date)} : 3 ${context.getString(R.string.to)} 5 ${
                            context.getString(
                                R.string.working_days
                            )
                        }"

                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                    }
                    "2.1" -> {
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#1A1AB364"))
                        btn.setTextColor(Color.parseColor("#17854C"))
                        btn.text = "${context.getString(R.string.expected_delivery_date)} : 3 ${context.getString(R.string.to)} 5 ${
                            context.getString(
                                R.string.working_days
                            )
                        }"

                        tvStatus.text = context.getString(R.string.ongoing)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
  "2.2" -> {
      btn.backgroundTintList =
          ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
      btn.setTextColor(Color.parseColor("#FF5F1B"))
      btn.text = "${context.getString(R.string.exchange_date)} : 12 SEP 25"

      tvStatus.text = context.getString(R.string.ongoing)
      tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "7" -> {
                        when (orderData?.returnType) {
                            "1" -> {
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                                btn.setTextColor(Color.parseColor("#FF5F1B"))
                                btn.text = "${context.getString(R.string.return_date)} : ${CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd MMM yy")}"
                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            "2" -> {
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                                btn.setTextColor(Color.parseColor("#FF5F1B"))
                                btn.text = "${context.getString(R.string.exchange_date)} : 12 SEP 25"

                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            else -> {
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#1A1AB364"))
                                btn.setTextColor(Color.parseColor("#17854C"))
                                btn.text = "${context.getString(R.string.expected_delivery_date)} : 3 ${context.getString(R.string.to)} 5 ${
                                    context.getString(
                                        R.string.working_days
                                    )
                                }"

                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                        }

                    }

                    "3" -> {

                        if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "0") {
                            btn.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                            btn.setTextColor(Color.parseColor("#16B364"))
                            btn.text = "${context.getString(R.string.delivered_on)} : ${
                                CommonMethod.customizeChangeDateFormat(
                                    orderData?.updatedAt.toString(),
                                    outputFormat = "dd MMM yy"
                                )
                            }"

                            tvStatus.text = context.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "1" && orderData?.returnType == "2") {

                            if (orderData?.exchangeOrderDetails?.deliveryStatus=="7"){
                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                                btn.setTextColor(Color.parseColor("#FF5F1B"))
                                btn.text = "${context.getString(R.string.pickup_date)} : ${
                                    CommonMethod.customizeChangeDateFormat(
                                        orderData?.updatedAt.toString(),
                                        outputFormat = "dd MMM yy"
                                    )
                                }"
                            }
                            else if (orderData?.exchangeOrderDetails?.deliveryStatus=="3"){
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                                btn.setTextColor(Color.parseColor("#16B364"))
                                btn.text = "${context.getString(R.string.exchanged_on)} : ${
                                    CommonMethod.customizeChangeDateFormat(
                                        orderData?.updatedAt.toString(),
                                        outputFormat = "dd MMM yy"
                                    )
                                }"

                                tvStatus.text = context.getString(R.string.exchanged)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }
                            else{
                                btn.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                                btn.setTextColor(Color.parseColor("#FF5F1B"))
                                btn.text = "${context.getString(R.string.pickup_date)} : 20 JAN 26"

                                tvStatus.text = context.getString(R.string.exchange_req)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                        }
                        else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "2") {
                            btn.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.pickup_date)} : 20 JAN 26"

                            tvStatus.text = context.getString(R.string.exchange_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }

                        else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="0"&&orderData?.returnType=="1"){
                            btn.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.pickup_date)} : 20 JAN 26"

                            tvStatus.text = context.getString(R.string.return_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="1"&&orderData?.returnType=="1"){
                            tvStatus.text = context.getString(R.string.returned)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                            btn.setTextColor(Color.parseColor("#FF5F1B"))
                            btn.text = "${context.getString(R.string.return_date)} : ${
                                CommonMethod.customizeChangeDateFormat(
                                    orderData?.updatedAt.toString(),
                                    outputFormat = "dd MMM yy"
                                )
                            }"

                        }
                        else{
                            btn.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                            btn.setTextColor(Color.parseColor("#16B364"))
                            btn.text = "${context.getString(R.string.delivered_on)} : ${
                                CommonMethod.customizeChangeDateFormat(
                                    orderData?.updatedAt.toString(),
                                    outputFormat = "dd MMM yy"
                                )
                            }"

                            tvStatus.text = context.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                    }

                    "4" -> {
                        btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F4DDDD"))
                        btn.setTextColor(Color.parseColor("#E70000"))
                        btn.text = "${context.getString(R.string.cancelled_date)} : ${CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd MMM yy")}"

                        tvStatus.text = context.getString(R.string.cancelled)
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                    }

                    "5" -> {
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                        btn.setTextColor(Color.parseColor("#FF5F1B"))
                        btn.text = "${context.getString(R.string.pickup_date)} : 21 JAN 26"

                        tvStatus.text = context.getString(R.string.return_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "6" -> {
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                        btn.setTextColor(Color.parseColor("#17854C"))
                        btn.text = "${context.getString(R.string.expected_delivery_date)} : 3 ${context.getString(R.string.to)} 5 ${
                            context.getString(
                                R.string.working_days
                            )
                        }"

                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "8" -> {
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                        btn.setTextColor(Color.parseColor("#FF5F1B"))
                        btn.text = "${context.getString(R.string.pickup_date)} : 20 JAN 26"

                        tvStatus.text = context.getString(R.string.not_delivered)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "9" -> {
                        btn.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                        btn.setTextColor(Color.parseColor("#FF5F1B"))
                        btn.text = "${context.getString(R.string.pickup_date)} : 21 JAN 26"

                        tvStatus.text = context.getString(R.string.exchange_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
                }
            }
        }

        holder.itemView.setOnClickListener{
            orderClick.onOrderClick(position)
        }

    }

}

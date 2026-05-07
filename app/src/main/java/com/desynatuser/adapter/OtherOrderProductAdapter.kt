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
import com.desynatuser.databinding.ItemOrderProductBinding
import com.desynatuser.ui.home.myOrder.response.GetSingleOrderSummaryResponse
import com.desynatuser.utils.CommonMethod
import java.time.Duration
import java.time.Instant

class OtherOrderProductAdapter(var context:Context, private var productList: ArrayList<GetSingleOrderSummaryResponse.Data.AllOtherOrderVarient>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<OtherOrderProductAdapter.ViewHolder>() {

   interface ClickOnItem{
       fun onClickOnItem(position: Int,type:Int)
   }

    inner class ViewHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var productData = productList[position]

        holder.binding.apply {
            tvStatus.isVisible = false
            if (productData?.paymentStatus=="2"||productData?.paymentStatus=="3"){
                tvStatus.text = context.getString(R.string.cancelled)
                tvStatus.setTextColor(Color.parseColor("#E70000"))
            }
            else {
                when (productData?.deliveryStatus) {//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                    "0" -> {
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                    }
                    "1" -> {

                        if (productData?.rEApprove=="1"&&productData?.returnType=="1"&&productData?.assignStatus=="0"){
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else if (productData?.rEApprove=="1"&&productData?.returnType=="1"&&productData?.assignStatus=="1"){
                            tvStatus.text = context.getString(R.string.ongoing)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else{
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                        if (productData?.rEApprove == "1" && productData?.returnType == "1" && productData?.assignStatus == "0") {
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))

                        } else if (productData?.rEApprove == "1" && productData?.returnType == "1" && productData?.assignStatus == "1") {
                            tvStatus.text = context.getString(R.string.out_for_picked)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))

                        } else {
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                        }
                    }

                    "2" -> {
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                    }
                    "2.1" -> {
                        tvStatus.text = context.getString(R.string.ongoing)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
                    "2.2" -> {
                        tvStatus.text = context.getString(R.string.ongoing)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "7" -> {
                        when (productData?.returnType) {
                            "1" -> {
                               tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            "2" -> {
                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            else -> {
                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                        }

                    }

                    "3" -> {

                        if (productData?.r_e_type == "0" && productData?.p_orderdetails_id == "0" && productData?.rEApprove == "0" && productData?.returnType == "0") {
                            tvStatus.text = context.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else if (productData?.r_e_type == "0" && productData?.p_orderdetails_id == "0" && productData?.rEApprove == "1" && productData?.returnType == "2") {

                            if (productData?.exchangeOrderDetails?.deliveryStatus=="7"){
                                tvStatus.text = context.getString(R.string.ongoing)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else if (productData?.exchangeOrderDetails?.deliveryStatus=="3"){
                                tvStatus.text = context.getString(R.string.exchanged)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }
                            else{
                                tvStatus.text = context.getString(R.string.exchange_req)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                        }
                        else if (productData?.r_e_type == "0" && productData?.p_orderdetails_id == "0" && productData?.rEApprove == "0" && productData?.returnType == "2") {
                            tvStatus.text = context.getString(R.string.exchange_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }

                        else if (productData?.r_e_type=="0"&&productData?.p_orderdetails_id=="0"&&productData?.rEApprove=="0"&&productData?.returnType=="1"){
                            tvStatus.text = context.getString(R.string.return_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else if (productData?.r_e_type=="0"&&productData?.p_orderdetails_id=="0"&&productData?.rEApprove=="1"&&productData?.returnType=="1"){
                            tvStatus.text = context.getString(R.string.returned)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else{
                            tvStatus.text = context.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                    }

                    "4" -> {
                        tvStatus.text = context.getString(R.string.cancelled)
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                    }

                    "5" -> {
                        tvStatus.text = context.getString(R.string.return_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "6" -> {
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "8" -> {
                        tvStatus.text = context.getString(R.string.not_delivered)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "9" -> {
                        tvStatus.text = context.getString(R.string.exchange_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
                }
            }

            if (productData?.orderType=="2"){
                clCustomOrder.isVisible = true
                clOrder.isVisible = false

                if (position == productList.size - 1) {
                    holder.binding.viewCustomBottom.isVisible = false
                } else {
                    holder.binding.viewCustomBottom.isVisible = true
                }

                tvRequestId.text = "${context.getString(R.string.request_id)} #${productData?.id}"
                tvCustomTitle.text = productData?.productDetails?.productNameEn

                tvCustomDetail.text = productData?.chatroomRecordtable?.customDetails
                tvTotalQuantityValue.text = productData?.chatroomRecordtable?.quantity

               // tvPerItemPriceValue.text = "${CommonMethod.formatPrice(productData?.amount?.toDoubleOrNull()?:0.0)} KD"

            //    tvPerItemPriceValue.text = "(${CommonMethod.formatPrice(productData?.chatroomRecordtable?.price?.toDoubleOrNull()?:0.0)} KD X ${productData?.chatroomRecordtable?.quantity} = ${CommonMethod.formatPrice(productData?.chatroomRecordtable?.totalAmount?.toDoubleOrNull()?:0.0)} KD)"
                tvPerItemPriceValue.text = "${CommonMethod.formatPrice(productData?.chatroomRecordtable?.totalAmount?.toDoubleOrNull()?:0.0) } KD"

                ivCustomStore.load(productData?.variationDetails?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                tvCustomSizeValue.text = productData?.variationDetails?.size?.name.toString()
                productData?.variationDetails?.color?.hexCode?.let {
                    ivCustomColor.setColorFilter(
                        Color.parseColor(
                            it
                        )
                    )
                }
            }
            else {
                clCustomOrder.isVisible = false
                clOrder.isVisible = true

                if (position == productList.size - 1) {
                    holder.binding.viewBottom.isVisible = false
                } else {
                    holder.binding.viewBottom.isVisible = true
                }
                tvTitle.text = productData?.productDetails?.productNameEn

                tvPrice.text = "${CommonMethod.formatCurrencyAmount(context,productData?.variationDetails?.actualPrice?.toDoubleOrNull()?:0.0)}"
                tvDiscountPrice.text = "${CommonMethod.formatCurrencyAmount(context,productData?.variationDetails?.finalPrice?.toDoubleOrNull()?:0.0)}"
                val returnDays = productData?.productDetails?.returnDays
                val exchangeDays = productData?.productDetails?.exchangableDays
                val finalText = when {
                    returnDays != null && exchangeDays != null ->
                        "${returnDays} ${context.getString(R.string.days_return_and)} ${exchangeDays} ${
                            context.getString(
                                R.string.days_exchange
                            )
                        }"

                    returnDays != null ->
                        "${returnDays} ${context.getString(R.string.days_return_and)} ${context.getString(R.string.no_exchange)}"

                    exchangeDays != null ->
                        "${context.getString(R.string.no_return)} ${exchangeDays} ${context.getString(R.string.days_exchange)}"

                    else ->
                        "${context.getString(R.string.no_return)} ${context.getString(R.string.no_exchange)}"
                }


                tvReturnPolicy.text = finalText

                ivStore.load(productData?.variationDetails?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                tvSizeValue.text = productData?.variationDetails?.size?.name.toString()
                productData?.variationDetails?.color?.hexCode?.let {
                    ivColor.setColorFilter(
                        Color.parseColor(
                            it
                        )
                    )
                }
                tvQuantity.text = productData?.quantity.toString()

                val discountValue = productData?.variationDetails?.discountValue ?: 0
                val discountType =
                    if (productData?.variationDetails?.discountType == "1") "% ${context.getString(R.string.off)}" else " KD ${
                        context.getString(
                            R.string.off
                        )
                    }"
                tvDiscount.text = "$discountValue$discountType"

            }
            viewCustomImg.isVisible = false
            btnCustomCancelOrder.isVisible = false
            btnCancelOrder.isVisible = false
            tvExchange.isVisible = false
            tvReturn.isVisible = false
            relRating.isVisible = false
            relCustomRating.isVisible = false

        }

    }

    fun canCancelOrder(orderDate: String): Boolean {
        val orderInstant = Instant.parse(orderDate)
        val currentInstant = Instant.now()

        val hoursDiff = Duration.between(orderInstant, currentInstant).toHours()

        return hoursDiff < 3
    }

}

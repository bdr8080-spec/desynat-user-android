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

class OrderProductAdapter(var context:Context, private var productList: ArrayList<GetSingleOrderSummaryResponse.Data>,var deliveryStatus:String,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<OrderProductAdapter.ViewHolder>() {

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

            if (productData?.order?.orderType=="2"){
                clCustomOrder.isVisible = true
                clOrder.isVisible = false

                if (position == productList.size - 1) {
                    holder.binding.viewCustomBottom.isVisible = false
                } else {
                    holder.binding.viewCustomBottom.isVisible = true
                }

                tvRequestId.text = "Request Id #${productData?.order?.id}"
                tvCustomTitle.text = productData?.productDetails?.productNameEn
                tvCustomDetail.text = productData?.chatroomRecordtable?.customDetails
                tvTotalQuantityValue.text = productData?.chatroomRecordtable?.quantity
                tvAddressValue.text = productData?.order?.deliveryDetails?.address

                tvPerItemPriceValue.text = "${
                    CommonMethod.formatPrice(productData?.amount?:0.0)} KD"

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
                tvCustomQuantity.text = productData?.quantity.toString()

                if (productData?.paymentStatus == "2" || productData?.paymentStatus == "3") {
                    btnCustomCancelOrder.isVisible = false
                } else {
                    when (productData?.deliveryStatus) {//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                        "1" -> {
                            relCustomRating.isVisible = false

                            if (canCancelOrder(productData?.createdAt.toString())) {
                                btnCustomCancelOrder.isVisible = true
                            } else {
                                btnCustomCancelOrder.isVisible = false
                            }
                        }

                        "2" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }


                        "2.1" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }
                        "2.2" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }

                        "3" -> {
                            btnCustomCancelOrder.isVisible = false
                            if (productData?.userRatingReviewProvide != null) {
                                relCustomRating.isVisible = false
                            } else {
                                relCustomRating.isVisible = true
                            }
                        }

                        "7" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }

                        "8" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }

                        "4" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }

                        "5" -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }
                        else -> {
                            btnCustomCancelOrder.isVisible = false
                            relCustomRating.isVisible = false
                        }
                    }
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

                tvPrice.text = "${productData?.variationDetails?.actualPrice} KD"
                tvDiscountPrice.text = "${productData?.variationDetails?.finalPrice} KD"


                val returnDays = productData?.productDetails?.returnDays
                val exchangeDays = productData?.productDetails?.exchangableDays

                val finalText = when {
                    returnDays != null && exchangeDays != null ->
                        "${returnDays} Days Return & ${exchangeDays} Days Exchange"

                    returnDays != null ->
                        "${returnDays} Days Return & No Exchange"

                    exchangeDays != null ->
                        "No Return & ${exchangeDays} Days Exchange"

                    else ->
                        "No Return & No Exchange"
                }

                if (productData?.order?.orderType == "2") {
                    tvReturnPolicy.isVisible = false
                } else {
                    tvReturnPolicy.text = finalText
                }

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
                    if (productData?.variationDetails?.discountType == "1") "% off" else " KD off"
                tvDiscount.text = "$discountValue$discountType"

                if (productData?.paymentStatus == "2" || productData?.paymentStatus == "3") {
                    btnCancelOrder.isVisible = false
                } else {
                    when (productData?.deliveryStatus) {//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                        "1" -> {
                            btnCancelOrder.isVisible = true
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }

                        "2" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }
                        "2.1" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }
                        "2.2" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }

                        "3" -> {
                            if (productData?.order?.orderType == "2") {
                                btnCancelOrder.isVisible = false
                                tvReturn.isVisible = false
                                tvExchange.isVisible = false
                                if (productData?.userRatingReviewProvide != null) {
                                    relRating.isVisible = false
                                } else {
                                    relRating.isVisible = true
                                }
                            } else {

                                when (productData?.returnType) {
                                    "1" -> {
                                        btnCancelOrder.isVisible = false
                                        tvReturn.isVisible = false
                                        tvExchange.isVisible = false
                                        relRating.isVisible = false
                                    }

                                    "2" -> {
                                        btnCancelOrder.isVisible = false
                                        tvReturn.isVisible = false
                                        tvExchange.isVisible = false
                                        relRating.isVisible = false
                                    }

                                    else -> {
                                        btnCancelOrder.isVisible = false
                                        if (productData?.productDetails?.productReturn == "1") {
                                            if (productData?.returnType == "1") {
                                                tvReturn.isVisible = false
                                            } else {
                                                tvReturn.isVisible = true
                                            }

                                        }
                                        if (productData?.productDetails?.productExchangable == "1") {
                                            if (productData?.returnType == "2") {
                                                tvExchange.isVisible = false
                                            } else {
                                                tvExchange.isVisible = true
                                            }
                                        }

                                        if (productData?.userRatingReviewProvide != null) {
                                            relRating.isVisible = false
                                        } else {
                                            relRating.isVisible = true
                                        }
                                    }


                                }
                            }
                        }

                        "7" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }

                        "8" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }

                        "4" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }

                        "5" -> {
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }
                        else ->{
                            btnCancelOrder.isVisible = false
                            relRating.isVisible = false
                            tvExchange.isVisible = false
                            tvReturn.isVisible = false
                        }
                    }
                }
            }

            btnCustomCancelOrder.setOnClickListener {
                clickOnItem.onClickOnItem(position,1)
            }

            btnCancelOrder.setOnClickListener {
                clickOnItem.onClickOnItem(position,1)
            }

            tvExchange.setOnClickListener {
                clickOnItem.onClickOnItem(position,2)
            }

            tvReturn.setOnClickListener {
                clickOnItem.onClickOnItem(position,3)
            }

            relRating.setOnClickListener {
                clickOnItem.onClickOnItem(position,4)
            }

            relCustomRating.setOnClickListener {
                clickOnItem.onClickOnItem(position,4)
            }

        }

    }

    fun canCancelOrder(orderDate: String): Boolean {
        val orderInstant = Instant.parse(orderDate)
        val currentInstant = Instant.now()

        val hoursDiff = Duration.between(orderInstant, currentInstant).toHours()

        return hoursDiff < 3
    }

}

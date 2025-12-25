package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemOrderProductBinding
import com.desynatuser.ui.home.myOrder.response.GetSingleOrderSummaryResponse

class OrderProductAdapter(var context:Context, private var productList: ArrayList<GetSingleOrderSummaryResponse.Data.OrderDetail>,var deliveryStatus:String,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<OrderProductAdapter.ViewHolder>() {

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

            if (position==productList.size-1){
                holder.binding.viewBottom.isVisible = false
            }
            else{
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

            tvReturnPolicy.text = finalText

            ivStore.load(productData?.variationDetails?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvSizeValue.text = productData?.variationDetails?.size?.name.toString()
            productData?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(Color.parseColor(it)) }
            tvQuantity.text = productData?.quantity.toString()

            val discountValue = productData?.variationDetails?.discountValue ?: 0
            val discountType = if (productData?.variationDetails?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"

            when(deliveryStatus){//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                "1" -> {
                    btnCancelOrder.isVisible = true
                    relRating.isVisible = false
                    tvExchange.isVisible = false
                    tvReturn.isVisible = false
                }
                "2" -> {
                    btnCancelOrder.isVisible = true
                    relRating.isVisible = false
                    tvExchange.isVisible = false
                    tvReturn.isVisible = false
                }
                "3" -> {
                    btnCancelOrder.isVisible = false
                    if (productData?.productDetails?.productReturn=="1"){
                        if (productData?.returnType=="1"){
                            tvReturn.isVisible = false
                        }
                        else{
                            tvReturn.isVisible = true
                        }

                    }
                    if (productData?.productDetails?.productExchangable=="1"){
                        if (productData?.returnType=="2"){
                            tvExchange.isVisible = false
                        }
                        else{
                            tvExchange.isVisible = true
                        }
                    }

                    if (productData?.userRatingReviewProvide!=null){
                        relRating.isVisible = false
                    }
                    else{
                        relRating.isVisible = true
                    }
                }
                "7" -> {
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

        }

    }

}

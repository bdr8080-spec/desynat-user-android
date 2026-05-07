package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCustomBagBinding
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.CommonMethod

class MyCustomBagAdapter(var context:Context, private var cartList: ArrayList<GetCartResponse.Data.CartData>, var foodBagClick:FoodBagClick) : RecyclerView.Adapter<MyCustomBagAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCustomBagBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface FoodBagClick{
        fun onFoodBagClick(position: Int,type:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCustomBagBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var cartData = cartList[position]

        holder.binding.apply {

            if (position==cartList.size-1){
                holder.binding.viewBottom.isVisible = false
            }
            else{
                holder.binding.viewBottom.isVisible = true
            }
            tvTitle.text = cartData.product.productNameEn

            tvPrice.text = "${CommonMethod.formatCurrencyAmount(context,cartData?.productVariants?.actualPrice?.toDoubleOrNull()?:0.0)}"
         //   tvDiscountPrice.text = "${cartData?.productVariants?.finalPrice} KD"
            tvDiscountPrice.text = "${CommonMethod.formatCurrencyAmount(context,cartData?.chatroomData?.totalAmount?.toDoubleOrNull()?:0.0)}"
            val returnDays = cartData?.product?.returnDays
            val exchangeDays = cartData?.product?.exchangableDays

            val finalText = when {
                returnDays != null && exchangeDays != null ->
                    "${returnDays} ${context.getString(R.string.days_return_and)} ${exchangeDays} ${
                        context.getString(
                            R.string.days_exchange
                        )
                    }"

                returnDays != null ->
                    "${returnDays} ${context.getString(R.string.days_return)} ${context.getString(R.string.no_exchange)}"

                exchangeDays != null ->
                    "${context.getString(R.string.no_return)} ${exchangeDays} ${context.getString(R.string.days_exchange)}"

                else ->
                    "${context.getString(R.string.no_return)} ${context.getString(R.string.days_return)}"
            }

            tvReturnPolicy.text = finalText

            ivStore.load(cartData?.productVariants?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvSellerName.text = cartData?.product?.sellerDetails?.firstName+" "+ cartData?.product?.sellerDetails?.lastName
            tvDeliverBy.text = "3 to 5 working days"
            tvSizeValue.text = cartData?.productVariants?.size?.name.toString()
            ivColor.setColorFilter(Color.parseColor(cartData?.productVariants?.color?.hexCode))
            tvQuantity.text = cartData?.quantity.toString()
            tvCount.text = cartData?.quantity.toString()

            val discountValue = cartData?.productVariants?.discountValue ?: 0
            val discountType = if (cartData?.productVariants?.discountType == "1")"%  ${
                context.getString(
                    R.string.off
                )
            }" else " ${CommonMethod.getCurrency(context)}  ${context.getString(R.string.off)}"
            tvDiscount.text = "$discountValue$discountType"

            tvCustomDetail.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "custom")
            }

            ivDelete.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "remove")
            }
        }

    }

}

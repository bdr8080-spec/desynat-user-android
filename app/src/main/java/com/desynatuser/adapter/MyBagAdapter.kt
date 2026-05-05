package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemBagBinding
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants

class MyBagAdapter(var context:Context, private var cartList: ArrayList<GetCartResponse.Data.CartData>, var foodBagClick:FoodBagClick) : RecyclerView.Adapter<MyBagAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBagBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface FoodBagClick{
        fun onFoodBagClick(position: Int,type:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBagBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var cartData = cartList[position]

        holder.binding.apply {

            if (cartList[position].orderType=="2"){
                ivPlus.setAlpha(0.1f)
                ivMinus.setAlpha(0.1f)
                tvCustomDetail.isVisible = true
                ivDelete.isVisible = true
            }
            else{
                ivPlus.setAlpha(1f)
                ivMinus.setAlpha(1f)
                tvCustomDetail.isVisible = false
                ivDelete.isVisible = false
            }

            if (position==cartList.size-1){
                holder.binding.viewBottom.isVisible = false
            }
            else{
                holder.binding.viewBottom.isVisible = true
            }
            tvTitle.text = cartData.product.productNameEn


            tvPrice.text = "${CommonMethod.formatCurrencyAmount(context,cartData?.productVariants?.actualPrice?.toDoubleOrNull()?:0.0)}"
            tvDiscountPrice.text = "${CommonMethod.formatCurrencyAmount(context,cartData?.productVariants?.finalPrice?.toDoubleOrNull()?:0.0)}"

            val discountValue = "${CommonMethod.formatCurrencyAmountWithout(context,cartData?.productVariants?.discountValue?.toDoubleOrNull() ?: 0.0)}"
            val discountType = if (cartData?.productVariants?.discountType == "1")"% off" else " ${Constants.CURRENCY} off"
            tvDiscount.text = "$discountValue$discountType"

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

            if (cartData?.orderType=="2"){
                tvReturnPolicy.isVisible = false
                tvReturnPolicy.setTextColor(Color.parseColor("#000000"))
                tvReturnPolicy.text = "${context.getString(R.string.per_item_price)} ${CommonMethod.formatCurrencyAmount(context,cartData.chatroomData.price?.toDoubleOrNull()?:0.0)}"
            }
            else{
                tvReturnPolicy.setTextColor(Color.parseColor("#17854C"))
                tvReturnPolicy.text = finalText
            }

            ivStore.load(cartData?.productVariants?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvSellerName.text = cartData?.product?.sellerDetails?.firstName+" "+ cartData?.product?.sellerDetails?.lastName
          //  tvDeliverBy.text = CommonMethod.getExpectedDeliveryDate(output = "dd/MM/yyyy")
            tvDeliverBy.text = "3 to 5 working days"
            tvSizeValue.text = cartData?.productVariants?.size?.name.toString()
            ivColor.setColorFilter(android.graphics.Color.parseColor(cartData?.productVariants?.color?.hexCode))
            tvQuantity.text = cartData?.quantity.toString()
            tvCount.text = cartData?.quantity.toString()

            ivMinus.setOnClickListener {
                if (cartList[position].orderType=="1") {
                    foodBagClick.onFoodBagClick(position, "decrement")
                }
            }

            ivPlus.setOnClickListener {
                if (cartList[position].orderType=="1") {
                    foodBagClick.onFoodBagClick(position, "increment")
                }
            }

            tvCustomDetail.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "custom")
            }

            ivDelete.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "remove")
            }

        }

    }

}

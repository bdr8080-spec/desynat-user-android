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
import com.desynatuser.utils.Constants

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
            tvTitle.text =  if (Constants.language=="ar") cartData.product.productNameAr else cartData.product.productNameEn

            tvDiscountPrice.text = "${CommonMethod.formatCurrencyAmount(context,cartData?.chatroomData?.totalAmount?.toDoubleOrNull()?:0.0)}"

            val discountValueDouble = cartData?.productVariants?.discountValue?.toDoubleOrNull() ?: 0.0
            if (discountValueDouble > 0.0) {
                tvPrice.isVisible = true
                tvDiscount.isVisible = true
                tvPrice.text =
                    "${CommonMethod.formatCurrencyAmount(context, cartData?.productVariants?.actualPrice?.toDoubleOrNull() ?: 0.0)}"
                val discountValue =
                    "${CommonMethod.formatCurrencyAmountWithout(context, discountValueDouble)}"
                val discountType = if (cartData?.productVariants?.discountType == "1") "%  ${
                    context.getString(
                        R.string.off
                    )
                }" else " ${CommonMethod.getCurrency(context)}  ${context.getString(R.string.off)}"
                tvDiscount.text = "$discountValue$discountType"
            } else {
                tvPrice.isVisible = false
                tvDiscount.isVisible = false
            }

            val returnDays = cartData?.product?.returnDays

            tvCustomDetail.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "custom")
            }

            ivDelete.setOnClickListener {
                foodBagClick.onFoodBagClick(position, "remove")
            }
        }

    }

}

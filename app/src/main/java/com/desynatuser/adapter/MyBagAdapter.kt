package com.desynatuser.adapter

import android.content.Context
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
            }
            else{
                ivPlus.setAlpha(1f)
                ivMinus.setAlpha(1f)
                tvCustomDetail.isVisible = false
            }

            if (position==cartList.size-1){
                holder.binding.viewBottom.isVisible = false
            }
            else{
                holder.binding.viewBottom.isVisible = true
            }
            tvTitle.text = cartData.product.productNameEn

            tvPrice.text = "${cartData?.productVariants?.actualPrice} KD"
            tvDiscountPrice.text = "${cartData?.productVariants?.finalPrice} KD"


            val returnDays = cartData?.product?.returnDays
            val exchangeDays = cartData?.product?.exchangableDays

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

            /*

                        if (cartData?.product?.returnDays!=null) {
                            tvReturnPolicy.text = "${cartData?.product?.returnDays} days return available"
                            tvReturnPolicy.isVisible = true
                        }
                        else{
                            tvReturnPolicy.isVisible = false
                        }
            */

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

            val discountValue = cartData?.productVariants?.discountValue ?: 0
            val discountType = if (cartData?.productVariants?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"

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

        }

    }

}

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemFoodBagBinding
import com.desynatuser.ui.home.store.response.GetCartResponse

class MyBagAdapter(var context:Context, private var cartList: ArrayList<GetCartResponse.Data.CartData>, var foodBagClick:FoodBagClick) : RecyclerView.Adapter<MyBagAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFoodBagBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface FoodBagClick{
        fun onFoodBagClick(position: Int,type:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFoodBagBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var cartData = cartList[position]

        holder.binding.apply {
            tvTitle.text = cartData.product.productNameEn

            tvPrice.text = "${cartData?.productVariants?.actualPrice} KD"
            tvDiscountPrice.text = "${cartData?.productVariants?.finalPrice} KD"
            tvReturnPolicy.text = "${cartData?.product?.returnDays} days return available"

            ivStore.load(cartData?.productVariants?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvSellerName.text = cartData?.product?.sellerDetails?.firstName+" "+ cartData?.product?.sellerDetails?.lastName
          //  tvDeliverBy.text = cartData?.product
            tvSizeValue.text = cartData?.productVariants?.size?.name.toString()
            ivColor.setColorFilter(android.graphics.Color.parseColor(cartData?.productVariants?.color?.hexCode))
            tvQuantity.text = cartData?.quantity.toString()
            tvCount.text = cartData?.quantity.toString()

            ivMinus.setOnClickListener {
                foodBagClick.onFoodBagClick(position,"decrement")
            }

            ivPlus.setOnClickListener {
                foodBagClick.onFoodBagClick(position,"increment")
            }

        }

    }

}

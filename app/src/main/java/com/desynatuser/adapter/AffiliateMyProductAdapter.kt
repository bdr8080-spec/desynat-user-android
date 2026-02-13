package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemAffiliateProductBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.CommonMethod

class AffiliateMyProductAdapter(var context:Context, private var productList: ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>, var productClick:ProductClick) : RecyclerView.Adapter<AffiliateMyProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]

        holder.binding.apply {
//            if (storeName!=""){
//                tvTitle.text = productData.productNameEn+" (By ${storeName})"
//            }
//            else{
//                tvTitle.text = productData.productNameEn
//            }

            tvPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.actualPrice?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.finalPrice?:0.0)} KD"

            val variant = productData?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"
            tvCommission.text = "${productData?.productVariants?.get(0)?.discountValue}% Commission"
            tvGetLink.text = "Track Performance"

            ivStore.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvGetLink.setOnClickListener {
                productClick.onProductClick(position,1)
            }
        }

    }

}

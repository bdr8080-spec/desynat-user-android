package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemAffiliateMyProductBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductResponse
import com.desynatuser.utils.CommonMethod

class AffiliateMyProductAdapter(var context:Context, private var productList: ArrayList<GetAffiliateMyProductResponse.Data.Data>, var productClick:ProductClick) : RecyclerView.Adapter<AffiliateMyProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateMyProductBinding) :
        RecyclerView.ViewHolder(binding.root)
    private var isCategoryAvailable = false
    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateMyProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position].product

        holder.binding.apply {
            if (productData?.store?.storeName!=null){
                tvTitle.text = productData?.productNameEn+" (By ${productData?.store?.storeName})"
            }
            else{
                tvTitle.text = productData?.productNameEn
            }
            tvPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.actualPrice?.toDoubleOrNull()?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.finalPrice?.toDoubleOrNull()?:0.0)} KD"

            val variant = productData?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"
            tvCommission.text = "${productData?.affiliatedCommission?:0}% Commission"

            tvRating.text = "(${productData?.storeAvgRating?:0})"
            rating.rating = productData?.storeAvgRating?.toFloatOrNull()?:0f

            ivStore.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvTrackPerformance.isVisible = true
            tvTrackPerformance1.isVisible = false

//            if (isCategoryAvailable){
//                tvTrackPerformance.isVisible = true
//                tvTrackPerformance1.isVisible = false
//            }
//            else{
//                tvTrackPerformance.isVisible = false
//                tvTrackPerformance1.isVisible = true
//            }

            tvTrackPerformance.setOnClickListener {
                productClick.onProductClick(position,1)
            }

            tvTrackPerformance1.setOnClickListener {
                productClick.onProductClick(position,1)
            }
        }

    }

    fun setData(isAvailable: Boolean){
        isCategoryAvailable = isAvailable
        notifyDataSetChanged()
    }
}

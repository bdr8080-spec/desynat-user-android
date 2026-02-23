package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemAffiliateProductBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.utils.CommonMethod

class AffiliateProductAdapter(var context:Context, private var productList: ArrayList<GetAffiliateAllProductResponse.Data.Data>, var productClick:ProductClick) : RecyclerView.Adapter<AffiliateProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateProductBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int) = with(binding) {
                val productData = productList[position]

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
                    tvCommission.text = "${productData?.productVariants?.get(0)?.discountValue}% Commission"

                    ivStore.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }

                tvRating.text = "(${productData?.averageRating?.product?:0})"
                rating.rating = productData?.averageRating?.product?.toFloatOrNull()?:0f

                    tvGetLink.setOnClickListener {
                        productClick.onProductClick(position,2)
                    }

                root.setOnClickListener {
                    productClick.onProductClick(position,1)
                }
                }

        }

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
        holder.bind(position)

    }

}

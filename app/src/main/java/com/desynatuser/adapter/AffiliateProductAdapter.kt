package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemAffiliateProductBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants

class AffiliateProductAdapter(var context:Context, private var productList: ArrayList<GetAffiliateAllProductResponse.Data.Data>, var productClick:ProductClick) : RecyclerView.Adapter<AffiliateProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateProductBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int) = with(binding) {
                val productData = productList[position]

                if (productData?.store?.storeName!=null){
                    tvTitle.text = if (Constants.language=="ar") productData?.productNameAr+" (${context.getString(R.string.by)} ${productData?.store?.storeName})" else productData?.productNameEn+" (${context.getString(R.string.by)} ${productData?.store?.storeName})"
                }
                else{
                    tvTitle.text = if (Constants.language=="ar") productData?.productNameAr else productData?.productNameEn
                }
                    tvDiscountPrice.text = "${CommonMethod.formatCurrencyAmount(context,productData?.productVariants?.firstOrNull()?.finalPrice?.toDoubleOrNull()?:0.0)}"

                    val variant = productData?.productVariants?.getOrNull(0)
                    val discountValueDouble = variant?.discountValue?.toDoubleOrNull() ?: 0.0
                    if (discountValueDouble > 0.0) {
                        tvPrice.visibility = View.VISIBLE
                        tvDiscount.visibility = View.VISIBLE
                        tvPrice.text =
                            "${CommonMethod.formatCurrencyAmount(context, variant?.actualPrice?.toDoubleOrNull() ?: 0.0)}"
                        val discountValue =
                            "${CommonMethod.formatCurrencyAmountWithout(context, discountValueDouble)}"

                        val discountType = if (variant?.discountType == "1") "%  ${context.getString(R.string.off)}" else " ${
                            CommonMethod.getCurrency(context)
                        }  ${
                            context.getString(
                                R.string.off
                            )
                        }"
                        tvDiscount.text = "$discountValue$discountType"
                    } else {
                        tvPrice.visibility = View.GONE
                        tvDiscount.visibility = View.GONE
                    }
                   tvCommission.text = "${CommonMethod.formatCurrencyAmountWithout(context,productData?.affiliatedCommission?.toDoubleOrNull() ?: 0.0)}% ${context.getString(R.string.commission)}"


                ivStore.load(productData?.productVariants?.firstOrNull()?.variantImage?.firstOrNull()) {
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

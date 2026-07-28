package com.desynatuser.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemProductBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants

class ProductAdapter(
    var context: Context,
    private var productList: ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>,
    var storeName: String,
    var from: Int,
    var productClick: ProductClick
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ProductClick {
        fun onProductClick(position: Int, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]

        holder.binding.apply {
            val name = if (storeName != "") storeName else productData.store?.storeName ?: ""
            if (name != "") {
                tvTitle.text =
                    if (Constants.language == "ar") productData.productNameAr + " (${
                        context.getString(
                            R.string.by
                        )
                    } ${name})" else productData.productNameEn + " (${
                        context.getString(R.string.by)
                    } ${name})"
            } else {
                tvTitle.text =
                    if (Constants.language == "ar") productData.productNameAr else productData.productNameEn
            }

            tvPrice.text =
                "${
                    CommonMethod.formatCurrencyAmount(
                        context,
                        productData?.productVariants?.get(0)?.actualPrice ?: 0.0
                    )
                }"
            tvDiscountPrice.text = "${
                CommonMethod.formatCurrencyAmount(
                    context,
                    productData?.productVariants?.get(0)?.finalPrice ?: 0.0
                )
            }"

            val variant = productData?.productVariants?.getOrNull(0)
            val discountValueDouble = variant?.discountValue?.toDoubleOrNull() ?: 0.0

            if (discountValueDouble > 0.0) {
                tvPrice.visibility = View.VISIBLE
                tvDiscount.visibility = View.VISIBLE

                tvPrice.text =
                    CommonMethod.formatCurrencyAmount(
                        context,
                        variant?.actualPrice ?: 0.0
                    )

                val discountValue = CommonMethod.formatCurrencyAmountWithout(
                    context,
                    discountValueDouble
                )
                val discountType =
                    if (variant?.discountType == "1") "%  ${context.getString(R.string.off)}" else " ${
                        CommonMethod.getCurrency(context)
                    }  ${
                        context.getString(
                            R.string.off
                        )
                    }"
                tvDiscount.text = "$discountValue$discountType"
            } else {
                tvPrice.visibility = View.INVISIBLE
                tvDiscount.visibility = View.INVISIBLE
                ViewLine.visibility = View.INVISIBLE
            }

            if (productData?.averageRating != null) {
                tvRating.text = "(${productData?.averageRating})"
            } else {
                tvRating.text = "(0)"
            }
            rating.rating = productData?.averageRating?.toFloatOrNull() ?: 0f

            Glide.with(context).load(productData?.productVariants?.get(0)?.variantImage?.get(0))
                .placeholder(R.drawable.ic_placeholder)
                .into(ivStore)


            if (from != 1) {
                if (productData.wishlist == 1) {
                    ivLike.setImageResource(R.drawable.ic_like_filled)
                } else {
                    ivLike.setImageResource(R.drawable.ic_like)
                }
            } else {
                ivLike.setImageResource(R.drawable.ic_like_filled)
            }

            ivLike.setOnClickListener {
                productClick.onProductClick(position, 2)
            }

        }

        holder.itemView.setOnClickListener {
            productClick.onProductClick(position, 1)

        }
    }

    fun updateStoreName(name: String) {
        storeName = name
        notifyDataSetChanged()
    }

}

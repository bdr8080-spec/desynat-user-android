package com.desynatuser.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemTopRatedBinding
import com.desynatuser.ui.home.fragment.response.GetTopRatedProductResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants

class TopRatedAdapter(
    private val context: Context,
    private val topRatedList: List<GetTopRatedProductResponse.Data>,
    var onClick: OnClick
) :
    RecyclerView.Adapter<TopRatedAdapter.ViewHolder>() {
    interface OnClick {
        fun onItemClick(position: Int, type: Int)
    }

    inner class ViewHolder(val binding: ItemTopRatedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {

            val productData = topRatedList[position]
            var storeName = productData?.store?.storeName ?: ""
            if (storeName != "") {
                tvTitle.text = if (Constants.language == "ar") productData.productNameAr + " (${
                    context.getString(R.string.by)
                } ${storeName})" else productData.productNameEn + " (${context.getString(R.string.by)} ${storeName})"
            } else {
                tvTitle.text = if (Constants.language == "ar") productData.productNameAr + " (${
                    context.getString(R.string.by)
                } ${storeName})" else productData.productNameEn + " (${context.getString(R.string.by)} ${storeName})"
            }
            tvDiscountPrice.text = "${
                CommonMethod.formatCurrencyAmount(
                    context,
                    productData?.productVariants?.get(0)?.finalPrice?.toDoubleOrNull() ?: 0.0
                )
            }"

            val variant = productData?.productVariants?.getOrNull(0)
            val discountValueDouble = variant?.discountValue?.toDoubleOrNull() ?: 0.0
            if (discountValueDouble > 0.0) {
                tvPrice.visibility = View.VISIBLE
                tvDiscount.visibility = View.VISIBLE
                tvPrice.text =
                    "${
                        CommonMethod.formatCurrencyAmount(
                            context,
                            variant?.actualPrice?.toDoubleOrNull() ?: 0.0
                        )
                    }"
                val discountValue =
                    "${CommonMethod.formatCurrencyAmountWithout(context, discountValueDouble)}"
                val discountType =
                    if (variant?.discountType == "1") "% ${context.getString(R.string.off)}" else " ${
                        CommonMethod.getCurrency(context)
                    } ${
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

            ivProfile.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            if (productData?.productAvgRating != null) {
                tvRating.text = "(${productData?.productAvgRating})"
            } else {
                tvRating.text = "(0)"
            }
            rating.rating = productData?.productAvgRating?.toFloatOrNull() ?: 0f

            if (productData.wishlist == 1) {
                ivLike.setImageResource(R.drawable.ic_like_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like)
            }

            ivLike.setOnClickListener {
                onClick.onItemClick(position, 1)
            }
            ivShare.setOnClickListener {
                onClick.onItemClick(position, 2)
            }
            root.setOnClickListener {
                onClick.onItemClick(position, 3)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTopRatedBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = topRatedList.size
}

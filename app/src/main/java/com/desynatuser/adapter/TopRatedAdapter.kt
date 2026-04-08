package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemCategoriesBinding
import com.desynatuser.databinding.ItemTopRatedBinding
import com.desynatuser.ui.home.fragment.response.GetTopRatedProductResponse
import com.desynatuser.utils.CommonMethod

class TopRatedAdapter(private val context: Context, private val topRatedList: List<GetTopRatedProductResponse.Data>) :
//class TopRatedAdapter(private val context: Context, private val topRatedList: List<String>) :
    RecyclerView.Adapter<TopRatedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTopRatedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {

            val productData = topRatedList[position]
            tvTitle.text = productData?.productNameEn
            tvPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.actualPrice?.toDoubleOrNull()?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(productData?.productVariants?.get(0)?.finalPrice?.toDoubleOrNull()?:0.0)} KD"
            val variant = productData?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"

            ivProfile.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemTopRatedBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = topRatedList.size
}

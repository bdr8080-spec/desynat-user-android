package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemProductBinding
import com.desynatuser.databinding.ItemStoreBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse

class SimilarProductAdapter(var context:Context, private var productList: ArrayList<String>, var storeName:String, var productClick:ProductClick) : RecyclerView.Adapter<SimilarProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ProductClick{
        fun onProductClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var productData = productList[position]

        holder.binding.apply {
           /* tvTitle.text = productData.productNameEn+" (By ${storeName})"

            tvPrice.text = "${productData?.productVariants?.get(0)?.actualPrice} KD"
            tvDiscountPrice.text = "${productData?.productVariants?.get(0)?.finalPrice} KD"

            val variant = productData?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1") "% off" else "KD off"
            tvDiscount.text = "$discountValue $discountType"

            ivStore.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }*/

        }

        holder.itemView.setOnClickListener {
            productClick.onProductClick(position)

        }
    }

}

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemProductImageBinding

class ProductImageAdapter(
    var context: Context, var productList: ArrayList<String>, var click: OnClick
) : RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {

    interface OnClick {
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
//            ivProduct.load(productData) {
//                crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_placeholder)
//            }
            Glide.with(context).load(productData).placeholder(R.drawable.ic_placeholder)
                .into(ivProduct)

            Glide.with(context).load(productData).placeholder(R.drawable.ic_placeholder)
                .into(ivProductBlur)


            if (isVideo(productData)) {
                ivPlay.visibility = android.view.View.VISIBLE
            } else {
                ivPlay.visibility = android.view.View.GONE
            }

            Glide.with(context)
                .load(productData)
                .override(50, 50) // Load tiny version for "blurred" look
                .into(ivProductBlur)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                ivProductBlur.setRenderEffect(
                    android.graphics.RenderEffect.createBlurEffect(
                        20f, 20f, android.graphics.Shader.TileMode.CLAMP
                    )
                )
            }

            ivProduct.setOnClickListener {
                click.onClick(position)
            }

        }
    }

    private fun isVideo(url: String): Boolean {
        val cleanUrl = url.substringBefore("?").substringBefore("#").lowercase()

        return cleanUrl.endsWith(".mp4") || cleanUrl.endsWith(".m3u8") || cleanUrl.endsWith(".mov") || cleanUrl.endsWith(
            ".avi"
        ) || cleanUrl.endsWith(".mkv") || cleanUrl.endsWith(".webm") || cleanUrl.endsWith(".3gp")
    }


}

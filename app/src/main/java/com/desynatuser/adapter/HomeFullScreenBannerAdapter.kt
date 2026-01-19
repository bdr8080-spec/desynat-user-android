package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.disk.DiskCache
import coil.load
import coil.memory.MemoryCache
import com.desynatuser.R
import com.desynatuser.databinding.ItemFullScreenImageBinding

class HomeFullScreenBannerAdapter(var context:Context, var productList: ArrayList<String>, var click:OnClick) : RecyclerView.Adapter<HomeFullScreenBannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemFullScreenImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFullScreenImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
            ivProduct.load(productData, imageLoader) {
                placeholder(R.color.white)
                error(R.color.white)
                crossfade(true)
            }

            ivProduct.setOnClickListener {
                click.onClick(position)
            }

        }
    }
    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(100L * 1024 * 1024)
                .build()
        }
        .build()

}

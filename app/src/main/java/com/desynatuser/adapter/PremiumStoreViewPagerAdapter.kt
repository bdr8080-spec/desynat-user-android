package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.databinding.ItemDealBannerBinding
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse

class PremiumStoreViewPagerAdapter(
    val context: Context,
    private val bannerList: List<GetHomeScreenAllBannerResponse.Data>,
    var click: OnClick
) :
    RecyclerView.Adapter<PremiumStoreViewPagerAdapter.ViewHolder>() {
    interface OnClick {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemDealBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.binding.ivBanner.load(bannerList[position].uploadBanner)
        holder.binding.tvAd.isVisible = true
        holder.binding.tvAd.bringToFront()

        Glide.with(context).load(bannerList[position].uploadBanner).into(holder.binding.ivBanner)

        holder.binding.ivBanner.setOnClickListener {
            click.onItemClick(position)
        }

    }

    override fun getItemCount(): Int = bannerList.size
}

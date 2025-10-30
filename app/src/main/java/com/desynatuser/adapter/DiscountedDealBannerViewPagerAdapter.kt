package com.desynatuser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemBannerBinding
import com.desynatuser.databinding.ItemDealBannerBinding
import com.desynatuser.databinding.ItemHomeBannerBinding
import com.desynatuser.ui.auth.fragment.BannerData

class DiscountedDealBannerViewPagerAdapter(private val bannerList: List<Int>) :
    RecyclerView.Adapter<DiscountedDealBannerViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDealBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemDealBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivBanner.setImageResource(bannerList[position])

    }

    override fun getItemCount(): Int = bannerList.size
}

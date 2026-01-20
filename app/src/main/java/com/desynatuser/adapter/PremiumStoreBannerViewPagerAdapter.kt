package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.desynatuser.databinding.ItemBannerBinding
import com.desynatuser.databinding.ItemDealBannerBinding
import com.desynatuser.databinding.ItemHomeBannerBinding
import com.desynatuser.ui.auth.fragment.BannerData
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse

class PremiumStoreBannerViewPagerAdapter(val context: Context, private val bannerList: List<GetHomeScreenAllBannerResponse.Data>) :
    RecyclerView.Adapter<PremiumStoreBannerViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDealBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemDealBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(bannerList[position].uploadBanner).into(holder.binding.ivBanner)
    //    holder.binding.ivBanner.load(bannerList[position].uploadBanner)


    }

    override fun getItemCount(): Int = bannerList.size
}

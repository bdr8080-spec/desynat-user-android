package com.desynatuser.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemBannerBinding
import com.desynatuser.databinding.ItemHomeBannerBinding
import com.desynatuser.ui.auth.fragment.BannerData
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.ui.response.GetHomeScreenPopUpBanner

class HomeWomenBannerViewPagerAdapter(private val bannerList: List<GetHomeScreenAllBannerResponse.Data>) :
    RecyclerView.Adapter<HomeWomenBannerViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemHomeBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (bannerList[position].uploadBanner!=null&&bannerList[position].uploadBanner!="null"&&bannerList[position].uploadBanner!=""){
            holder.binding.ivBanner.load(bannerList[position].uploadBanner)
        }
        else{
            holder.binding.ivBanner.setImageResource(R.drawable.home_banner)
        }

    }

    override fun getItemCount(): Int = bannerList.size
}

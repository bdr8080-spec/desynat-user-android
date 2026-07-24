package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemHomeBannerBinding
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse

class HomeWomenBannerViewPagerAdapter(
    val context: Context,
    private val bannerList: List<GetHomeScreenAllBannerResponse.Data>,
    var click: OnClick
) :
    RecyclerView.Adapter<HomeWomenBannerViewPagerAdapter.ViewHolder>() {
    interface OnClick {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeBannerBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bannerList[position]
        holder.binding.tvAd.isVisible = true
        holder.binding.tvAd.bringToFront()

        if (bannerList[position].uploadBanner != null && bannerList[position].uploadBanner != "null" && bannerList[position].uploadBanner != "") {
            //   holder.binding.ivBanner.load(bannerList[position].uploadBanner)
            Glide.with(context).load(bannerList[position].uploadBanner)
                .into(holder.binding.ivBanner)
        } else {
            holder.binding.ivBanner.setImageResource(R.drawable.home_banner)
        }
        holder.binding.ivBanner.setOnClickListener {
            if (bannerList[position].uploadBanner != null && bannerList[position].uploadBanner != "null" && bannerList[position].uploadBanner != "") {
                click.onItemClick(position)
            }
        }


    }

    override fun getItemCount(): Int = bannerList.size
}

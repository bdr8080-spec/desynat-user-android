package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemGiftCardBinding
import com.desynatuser.databinding.ItemPromoCodeBinding
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.ui.home.profile.response.GetPromoCodeResponse
import com.desynatuser.utils.Constants

class PromoCodeAdapter(var context:Context, var promoCodeList: ArrayList<GetPromoCodeResponse.Data>, var promoCodeClick:PromoCodeClick) : RecyclerView.Adapter<PromoCodeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPromoCodeBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface PromoCodeClick{
        fun onPromoCodeClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPromoCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return promoCodeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var promoCodeData = promoCodeList[position]

        holder.binding.apply {
            tvName.text = if (Constants.language=="ar") promoCodeData.nameArabic else  promoCodeData.nameEnglish
            tvFeature.text = "${context.getString(R.string.flat)} ${promoCodeData.discountPercent}% ${context.getString(R.string.off_on_minimum_order)} ${promoCodeData.minOrderValue} KD"
            tvDescription.text = if (Constants.language=="ar")  promoCodeData.descriptionArabic else promoCodeData.descriptionEnglish
        }

        holder.itemView.setOnClickListener {
            promoCodeClick.onPromoCodeClick(position)

        }
    }

}

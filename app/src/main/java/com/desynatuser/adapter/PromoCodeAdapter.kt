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
            tvName.text = promoCodeData.nameEnglish
            tvFeature.text = "Flat ${promoCodeData.discountPercent}% off on minimum order ${promoCodeData.minOrderValue} KD"
            tvDescription.text = promoCodeData.descriptionEnglish
        }

        holder.itemView.setOnClickListener {
            promoCodeClick.onPromoCodeClick(position)

        }
    }

}

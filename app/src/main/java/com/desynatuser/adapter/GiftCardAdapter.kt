package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemGiftCardBinding
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.utils.Constants

class GiftCardAdapter(var context:Context, var giftCardList: ArrayList<GetAllGiftCardResponse.Data>, var giftCardClick:GiftCardClick) : RecyclerView.Adapter<GiftCardAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemGiftCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface GiftCardClick{
        fun onGiftCardClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGiftCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return giftCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var giftData = giftCardList[position]

        holder.binding.apply {
            tvGiftName.text = if (Constants.language=="ar") giftData?.giftDetails?.nameArabic?:giftData?.giftDetails?.nameEnglish else giftData?.giftDetails?.nameEnglish
            ivGift.load(giftData?.giftDetails?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            giftCardClick.onGiftCardClick(position)

        }
    }

    fun setData(newList: ArrayList<GetAllGiftCardResponse.Data>) {
        giftCardList = newList
        notifyDataSetChanged()
    }

}

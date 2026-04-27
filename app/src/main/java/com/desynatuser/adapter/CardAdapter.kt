package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemCardBinding
import com.desynatuser.databinding.ItemCurrencyBinding
import com.desynatuser.ui.home.card.response.GetSavedCardResponse
import com.desynatuser.utils.CommonMethod

class CardAdapter(var context:Context, var cardList: ArrayList<GetSavedCardResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    var selectedPos = 0
    inner class ViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val cardData = cardList[position]
            tvCardName.text = cardData?.cardHolderName
            tvCardNumber.text = CommonMethod.maskCardNumber(cardData?.cardNumber.toString())
            Glide.with(context).load(cardData?.cardImage).into(ivCard)

            if (selectedPos==position){
                ivRadio.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivRadio.setImageResource(R.drawable.ic_radio_unselected)
            }

            ivDelete.setOnClickListener {
                clickOnItem.onClickItem(position,1)
            }

            ivRadio.setOnClickListener {
                selectedPos = position
                clickOnItem.onClickItem(position,2)
                notifyDataSetChanged()
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int,type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

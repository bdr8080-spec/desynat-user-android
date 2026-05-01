package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.WalletTransactionBinding
import com.desynatuser.ui.home.myWallet.response.GetWalletHistoryResponse
import com.desynatuser.utils.CommonMethod

class WalletTransactionAdapter(var context:Context, var walletTransactionList: ArrayList<GetWalletHistoryResponse.Data.History>, var walletTransactionClick:WalletTransactionClick) : RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: WalletTransactionBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val walletData = walletTransactionList[position]
            tvCreditedOn.text = CommonMethod.customizeChangeDateFormat(walletData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy,hh:mma")
            when(walletData?.type){
                "1"->{
                    tvOrder.isVisible = false
                    tvOrderId.isVisible = false
                    tvType.text = context.getString(R.string.credit)
                    tvType.setTextColor(Color.parseColor("#17854C"))
                    tvTransactionId.text = walletData?.transactionId
                    tvAmount.text = "+${CommonMethod.formatCurrencyAmount(context,walletData?.amount?:0.0)}"
                }
                "2"->{
                    tvOrder.isVisible = true
                    tvOrderId.isVisible = true
                    tvType.text = context.getString(R.string.debit)
                    tvType.setTextColor(Color.parseColor("#D72E04"))
                    tvTransactionId.text = walletData?.transactionId
                    tvOrderId.text = walletData?.orderId
                    tvAmount.text = "-${CommonMethod.formatCurrencyAmount(context,walletData?.amount?:0.0)}"
                }
                "3"->{
                    tvOrder.isVisible = false
                    tvOrderId.isVisible = false
                    tvTransaction.text = context.getString(R.string.gift_card_no)
                    tvCredited.text = context.getString(R.string.redeemed_on)
                    tvType.text = context.getString(R.string.redeemed_gift_card)
                    tvType.setTextColor(Color.parseColor("#17854C"))
                    tvTransactionId.text = walletData?.giftCardNo
                    tvAmount.text = "+${CommonMethod.formatCurrencyAmount(context,walletData?.amount?:0.0)}"

                }
            }
        }
        }

    interface WalletTransactionClick{
        fun onWalletTransactionClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            WalletTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return walletTransactionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

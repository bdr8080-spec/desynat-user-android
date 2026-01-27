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
            tvTransactionId.text = walletData?.transactionId
            tvOrderId.text = walletData?.uniqueId
            tvCreditedOn.text = CommonMethod.customizeChangeDateFormat(walletData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy,hh:mma")
            when(walletData?.type){
                "1"->{
                    tvOrder.isVisible = false
                    tvOrderId.isVisible = false
                    tvType.text = "Credit"
                    tvType.setTextColor(Color.parseColor("#D72E04"))
                    tvAmount.text = "+${CommonMethod.formatPrice(walletData?.amount?:0.0)}KD"
                }
                "2"->{
                    tvOrder.isVisible = true
                    tvOrderId.isVisible = true
                    tvType.text = "Debit"
                    tvType.setTextColor(Color.parseColor("#17854C"))
                    tvAmount.text = "-${CommonMethod.formatPrice(walletData?.amount?:0.0)}KD"
                }
                "3"->{
                    tvOrder.isVisible = false
                    tvOrderId.isVisible = false
                    tvTransaction.text = "Gift Card No."
                    tvCredited.text = "Redeemed On"
                    tvType.text = "Redeemed Gift Card"
                    tvType.setTextColor(Color.parseColor("#17854C"))
                    tvAmount.text = "+${CommonMethod.formatPrice(walletData?.amount?:0.0)}KD"

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

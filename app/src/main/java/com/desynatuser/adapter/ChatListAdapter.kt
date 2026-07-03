package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemChatListBinding
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.utils.CommonMethod

class ChatListAdapter(var context:Context, var chatList: ArrayList<GetChatListResponse.Data>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        holder.binding.apply {

            tvName.text = chatData?.sellerDetails?.firstName+" "+chatData?.sellerDetails?.lastName
            ivUser.load(chatData?.sellerDetails?.profilePic){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvLastMsg.text = chatData?.lastChat
              /*  if (chatData.lastChatTime!=null&&chatData.lastChatTime!="null"&&chatData.lastChatTime!="") {
                tvTime.text = CommonMethod.formatChatTime(context,
                    chatData?.lastChatTime.toString(), inputType = "dd MMM yyyy, hh:mm a"
                )
            }
            else{
                tvTime.text = CommonMethod.formatChatTime(context,
                    chatData?.updatedAt.toString(), inputType = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                )
            }*/
            tvTime.text=chatData.lastChatTime

            if (chatData?.unreadCount!=0){
                relCount.isVisible = true
                tvCount.text = chatData?.unreadCount.toString()
            }
            else{
                relCount.isVisible = false
            }

          if(chatData.type=="2") {
              tvStatus.isVisible = true

              when (chatData.action) {
                  "0" -> {
                      if (chatData.customDetails != null) {

                          tvStatus.text = context.getString(R.string.waiting)
                          tvStatus.setTextColor(Color.parseColor("#EA9413"))
                          tvStatus.backgroundTintList =
                              ColorStateList.valueOf(Color.parseColor("#1AEA9413"))
                      } else {
                          tvStatus.isVisible = false
                         }
                  }
                  "1" -> {
                      tvStatus.text = context.getString(R.string.quotation_received)
                      tvStatus.setTextColor(Color.parseColor("#172B85"))
                      tvStatus.backgroundTintList =
                          ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                  }

                  "2" -> {
                      tvStatus.text = context.getString(R.string.quotation_received)
                      tvStatus.setTextColor(Color.parseColor("#172B85"))
                      tvStatus.backgroundTintList =
                          ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                  }

                  "3" -> {
                      tvStatus.text = context.getString(R.string.quotation_received)
                      tvStatus.setTextColor(Color.parseColor("#172B85"))
                      tvStatus.backgroundTintList =
                          ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                  }
                  "4"->{
                      tvStatus.text = context.getString(R.string.quotation_rejected)
                      tvStatus.setTextColor(Color.parseColor("#D53B36"))
                      tvStatus.backgroundTintList =
                          ColorStateList.valueOf(Color.parseColor("#1AD53B36"))
                  }
                  "5"->{
                      if (chatData?.productBuyStatus=="1"){
                          tvStatus.text = context.getString(R.string.order_placed)
                          tvStatus.setTextColor(Color.parseColor("#16B364"))
                          tvStatus.backgroundTintList =
                              ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                      }
                      else{
                          tvStatus.text = context.getString(R.string.quotation_accepted)
                          tvStatus.setTextColor(Color.parseColor("#16B364"))
                          tvStatus.backgroundTintList =
                              ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                      }

                  }
                  else ->{
                      tvStatus.isVisible = false
                  }
              }
          }

            else{
                tvStatus.isVisible = false
          }
        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }


}

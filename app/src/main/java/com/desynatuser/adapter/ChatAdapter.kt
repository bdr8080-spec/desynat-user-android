package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemChatBinding
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.utils.CommonMethod

class ChatAdapter(var context:Context, var chatList: ArrayList<ChatMessage>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        holder.binding.apply {

            if (chatData.by == "user") {
                relMy.isVisible = true
                relOther.isVisible = false
                tvMyMsg.text = chatData.message
                if (chatData.read_status==1){
                    ivRead.isVisible = true
                }
                else{
                    ivRead.isVisible = false
                }
                tvMyMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
            }
            else{
                relMy.isVisible = false
                relOther.isVisible = true
                tvOtherMsg.text = chatData.message
                tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
            }

        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }


}

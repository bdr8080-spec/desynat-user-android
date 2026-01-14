package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemChatBinding
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.toSafeInt
import com.desynatuser.ui.home.chat.toSafeString
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

            if (chatData.by.toSafeString() == "user") {
                relMy.isVisible = true
                relOther.isVisible = false

                if (chatData.type.toSafeInt()==1){
                    ivMyMsg.isVisible = true
                    tvMyMsg.isVisible = false
                    ivMyMsg.load(chatData.message.toSafeString()){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                else{
                    ivMyMsg.isVisible = false
                    tvMyMsg.isVisible = true
                    tvMyMsg.text = chatData.message.toSafeString()
                }

                if (chatData.read.toSafeInt()==1){
                    ivRead.isVisible = true
                }
                else{
                    ivRead.isVisible = false
                }
                tvMyMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")

            }
            else{
                relMy.isVisible = false
                relOther.isVisible = true
                if (chatData.type.toSafeInt()==1){
                    ivOtherMsg.isVisible = true
                    tvOtherMsg.isVisible = false
                    ivOtherMsg.load(chatData.message.toSafeString()){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                else{
                    ivOtherMsg.isVisible = false
                    tvOtherMsg.isVisible = true
                    tvOtherMsg.text = chatData.message.toSafeString()
                }
                tvOtherMsg.text = chatData.message.toSafeString()
                tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
            }

            ivMyMsg.setOnClickListener {
                chatClick.onChatClick(position)
            }

            ivOtherMsg.setOnClickListener {
                chatClick.onChatClick(position)
            }

        }

    }
}

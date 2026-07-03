package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemChatBinding
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.toSafeInt
import com.desynatuser.ui.home.chat.toSafeString
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.CommonMethod.formatTime
import com.desynatuser.utils.SharedPreference

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
            val isArabicApp = SharedPreference.get(context).language == "ar"
            val arText = chatData.messageAr.toSafeString()
            val enText = chatData.message.toSafeString()

            if (chatData.by.toSafeString() == "user") {
                relMy.isVisible = true
                relOther.isVisible = false

                if (chatData.type.toSafeInt()==1){
                    ivMyMsg.isVisible = true
                    tvMyMsg.isVisible = false
                    Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivMyMsg)
                }
                else{
                    ivMyMsg.isVisible = false
                    tvMyMsg.isVisible = true
                    tvMyMsg.text = when {
                        isArabicApp && arText.isNotEmpty() -> arText
                        enText.isNotEmpty() -> enText
                        else -> arText
                    }                }

                if (chatData.read.toSafeInt()==1){
                    ivRead.isVisible = true
                }
                else{
                    ivRead.isVisible = false
                }
                tvMyMsgTime.text = formatTime(chatData.times.toSafeString())

            }
            else{
                relMy.isVisible = false
                relOther.isVisible = true
                if (chatData.type.toSafeInt()==1){
                    ivOtherMsg.isVisible = true
                    tvOtherMsg.isVisible = false
                    Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivOtherMsg)
                }
                else{
                    ivOtherMsg.isVisible = false
                    tvOtherMsg.isVisible = true
                    tvOtherMsg.text = when {
                        isArabicApp && arText.isNotEmpty() -> arText
                        enText.isNotEmpty() -> enText
                        else -> arText
                    }
                }
                tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormatKuwait(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
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

package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.R
import com.desynatuser.databinding.ItemNotificationBinding
import com.desynatuser.ui.home.fragment.response.GetNotificationResponse
import com.desynatuser.utils.CommonMethod

class NotificationAdapter(var context:Context, var notificationList: ArrayList<GetNotificationResponse.Data.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val notificationData = notificationList[position]
            tvTitle.text = notificationData?.titleEnglish
            tvDescription.text = notificationData?.descriptionEnglish
            tvDate.text = CommonMethod.customizeChangeDateFormat(notificationData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")
            if (notificationData.isRead==1){
                ivBell.setImageResource(R.drawable.icon_read_bell)
                clMain.setBackgroundResource(R.drawable.notification_read_background)
            }
            else{
                ivBell.setImageResource(R.drawable.icon_unread_bell)
                clMain.setBackgroundResource(R.drawable.notification_unread_background)
            }

            root.setOnClickListener {
                if (notificationData.isRead!=1){
                    clickOnItem.onClickItem(position)
                }
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

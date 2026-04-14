package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatuser.databinding.ItemEventBinding
import com.desynatuser.ui.home.storyHighlights.response.GetLiveStreamingRoomResponse
import com.desynatuser.utils.CommonMethod
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EventAdapter(var context: Context, var list : List<GetLiveStreamingRoomResponse.Data>, val click : OnCLick) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventAdapter.ViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            tvName.text = item?.sellerDetails?.firstName+" "+item?.sellerDetails?.lastName
            tvLocation.text = item?.timeslotDetails?.countryInfo?.name
            Glide.with(ivProfileImage).load(item?.sellerDetails?.profilePic).transform(CircleCrop()).into(ivProfileImage)
            tvJoinedOn.text = "Joined on ${CommonMethod.customizeChangeDateFormat(item?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
            tvLiveTiming.text = "Live Timing ${CommonMethod.customizeChangeDateFormat(item?.timeslotDetails?.date.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yy")}"
            val isShow = isLiveTime(item.timeslotDetails)
            btnWatchLive.apply {
                alpha = if (isShow) 1f else 0.4f
                isClickable = isShow
                isEnabled = isShow
            }
            tvTime.text = "${CommonMethod.customizeChangeDateFormatKuwait(item?.timeslotDetails?.from.toString(),"hh:mm a","hh:mm a")}"
            btnWatchLive.setOnClickListener {
                click.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnCLick{
        fun onClick(position: Int)
    }

    private fun isLiveTime(timeslot: GetLiveStreamingRoomResponse.Data.TimeslotDetails): Boolean {
        return try {
            val date = timeslot.date
            val fromTime = timeslot.from
            val toTime = timeslot.to

            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.ENGLISH)

            val fromTimeDefault = CommonMethod.customizeChangeDateFormatKuwait(fromTime,"hh:mm a","hh:mm a")
            val toTimeDefault = CommonMethod.customizeChangeDateFormatKuwait(toTime,"hh:mm a","hh:mm a")

            val startDateTime = formatter.parse("$date $fromTimeDefault")
            val endDateTime = formatter.parse("$date $toTimeDefault")

            val currentTime = Date()

            currentTime.after(startDateTime) && currentTime.before(endDateTime)

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}



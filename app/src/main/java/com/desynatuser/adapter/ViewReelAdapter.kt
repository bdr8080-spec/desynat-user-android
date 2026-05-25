package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemViewReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import java.util.Locale

class ViewReelAdapter(var context: Context, private val reelList: List<GetSellerStoryResponse.Data>, var onClick: OnClick) :
    RecyclerView.Adapter<ViewReelAdapter.ViewHolder>() {
    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemViewReelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var runnable: Runnable? = null
        private val handler = android.os.Handler()

        fun bind(position: Int) = with(binding) {
            val item = reelList[position]

            val url = item.file ?: ""

            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov")
            val isGif = url.endsWith(".gif")

            if (isVideo) {

                ivStory.isVisible = false
                videoStory.isVisible = true
                videoLoader.isVisible = true
                videoStory.setVideoPath(url)

                videoStory.setOnPreparedListener { mp ->
                    binding.videoLoader.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
                    binding.tvCurrentTime.text = "00:00"

                    binding.tvRemainingTime.text =
                        "-${formatTime(videoStory.duration)}"

                    val videoWidth = mp.videoWidth
                    val videoHeight = mp.videoHeight

                    val viewWidth = videoStory.width
                    val viewHeight = videoStory.height

                    val videoRatio = videoWidth.toFloat() / videoHeight
                    val viewRatio = viewWidth.toFloat() / viewHeight

                    val layoutParams = videoStory.layoutParams

                    if (videoRatio > viewRatio) {
                        layoutParams.height = viewHeight
                        layoutParams.width = (viewHeight * videoRatio).toInt()
                    } else {
                        layoutParams.width = viewWidth
                        layoutParams.height = (viewWidth / videoRatio).toInt()
                    }

                    videoStory.layoutParams = layoutParams

                    mp.isLooping = true
                    videoLoader.isVisible = false
                    videoStory.start()

                    ivPlay.setImageResource(R.drawable.ic_pause)

                    seekBar.max = videoStory.duration

                    updateSeekBar()
                }

                videoStory.setOnInfoListener { _, what, _ ->

                    when (what) {

                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            videoLoader.isVisible = true
                        }

                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            videoLoader.isVisible = false
                        }
                    }

                    false
                }

                seekBar.setOnSeekBarChangeListener(
                    object : android.widget.SeekBar.OnSeekBarChangeListener {

                        override fun onProgressChanged(
                            seekBar: android.widget.SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {

                            if (fromUser) {
                                videoStory.seekTo(progress)
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
                    }
                )

            } else {

                ivStory.isVisible = true
                videoStory.isVisible = false

                if (isGif) {
                    Glide.with(context)
                        .asGif()
                        .load(url)
                        .into(ivStory)
                } else {
                    Glide.with(context)
                        .load(url)
                        .into(ivStory)
                }
            }

            tvTitle.text = item.caption ?: ""

            ivPlay.setOnClickListener {

                if (videoStory.isPlaying) {
                    videoStory.pause()
                    ivPlay.setImageResource(R.drawable.ic_play)
                } else {
                    videoStory.start()
                    ivPlay.setImageResource(R.drawable.ic_pause)
                    updateSeekBar()
                }
            }

                if (item.wishlist == 1) {
                    ivLike.setImageResource(R.drawable.ic_like_filled)
                } else {
                    ivLike.setImageResource(R.drawable.ic_like)
                }

            ivLike.setOnClickListener {
                onClick.onItemClick(position,1)
            }

            ivShare.setOnClickListener {
                onClick.onItemClick(position,2)
            }
        }

        private fun updateSeekBar() {

            runnable?.let {
                handler.removeCallbacks(it)
            }

            runnable = object : Runnable {

                override fun run() {

                    if (binding.videoStory.isPlaying) {

                        val currentPosition =
                            binding.videoStory.currentPosition

                        val duration =
                            binding.videoStory.duration

                        binding.seekBar.progress = currentPosition

                        binding.tvCurrentTime.text =
                            formatTime(currentPosition)

                        val remainingTime =
                            duration - currentPosition

                        binding.tvRemainingTime.text =
                            "-${formatTime(remainingTime)}"
                    }

                    handler.postDelayed(this, 500)
                }
            }

            handler.post(runnable!!)
        }

    }

    private fun formatTime(ms: Int): String {

        val totalSeconds = ms / 1000

        val minutes = totalSeconds / 60

        val seconds = totalSeconds % 60

        return String.format(
            Locale.ENGLISH,
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemViewReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = reelList.size
}

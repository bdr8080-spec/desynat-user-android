package com.desynatuser.ui.home.storyHighlights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentViewReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewReelFragment : BaseFragment() {
    private var binding: FragmentViewReelBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var reelData: GetSellerStoryResponse.Data?=null

    private val handler = android.os.Handler()
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewReelBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        val itemJson = arguments?.getString("storyData")

        if (!itemJson.isNullOrEmpty()) {
            reelData = Gson().fromJson(itemJson, GetSellerStoryResponse.Data::class.java)
        }
        binding?.apply {

            setReelLikeStatus()
            val url = reelData?.file ?: ""

            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov")
            val isGif = url.endsWith(".gif")

            if (isVideo) {
                videoLoader.isVisible = true
                ivStory.isVisible = false
                videoStory.isVisible = true

                videoStory.setVideoPath(url)


                videoStory.setOnPreparedListener { mp ->


                    videoStory.post {

                        val videoWidth = mp.videoWidth.toFloat()
                        val videoHeight = mp.videoHeight.toFloat()

                        val viewWidth = videoStory.width.toFloat()
                        val viewHeight = videoStory.height.toFloat()

                        val scale = maxOf(
                            viewWidth / videoWidth,
                            viewHeight / videoHeight
                        )

                        val scaledWidth = scale * videoWidth
                        val scaledHeight = scale * videoHeight

                        val layoutParams = videoStory.layoutParams
                        layoutParams.width = scaledWidth.toInt()
                        layoutParams.height = scaledHeight.toInt()
                        videoStory.layoutParams = layoutParams

                        // Center it
                        videoStory.translationX = (viewWidth - scaledWidth) / 2
                        videoStory.translationY = (viewHeight - scaledHeight) / 2
                    }

// for center crop
                /*    val videoWidth = mp.videoWidth
                    val videoHeight = mp.videoHeight

                    val viewWidth = videoStory.width
                    val viewHeight = videoStory.height

                    val scaleX: Float
                    val scaleY: Float

                    val videoRatio = videoWidth.toFloat() / videoHeight
                    val viewRatio = viewWidth.toFloat() / viewHeight

                    if (videoRatio > viewRatio) {
                        // video wider → scale height
                        scaleX = videoRatio / viewRatio
                        scaleY = 1f
                    } else {
                        // video taller → scale width
                        scaleX = 1f
                        scaleY = viewRatio / videoRatio
                    }

                    videoStory.scaleX = scaleX
                    videoStory.scaleY = scaleY*/


                    val duration = videoStory.duration
                    seekBar?.max = duration

                    runnable = object : Runnable {
                        override fun run() {
                            val currentPosition = videoStory.currentPosition
                            val duration = videoStory.duration

                            // FIX: handle last frame properly
                            if (currentPosition >= duration - 500) {
                                seekBar?.progress = duration
                               tvCurrentTime?.text = formatTime(duration)
                           //    tvRemainingTime?.text = "-00:00"
                                tvRemainingTime?.text = "${formatTime(duration)}"
                            } else {
                                seekBar?.progress = currentPosition
                                tvCurrentTime?.text = formatTime(currentPosition)
                           //     tvRemainingTime?.text = "-${formatTime(duration - currentPosition)}"
                                tvRemainingTime?.text = "${formatTime(duration)}"
                            }

                            handler.postDelayed(this, 500)
                        }
                    }

                    startSeekBarUpdate()

                    mp.isLooping = false
                    videoLoader.isVisible = false
                    videoStory.start()
                    ivPlay.setImageResource(R.drawable.ic_pause)
                }

                videoStory.setOnInfoListener { _, what, _ ->

                    when (what) {

                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            videoLoader.isVisible = true
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }

                        android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            videoLoader.isVisible = false
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }
                    }

                    false
                }

            } else {
                ivPlay.isVisible = false
                videoLoader.isVisible = false
                ivStory.isVisible = true
                videoStory.isVisible = false

                if (isGif) {
                    Glide.with(requireActivity())
                        .asGif()
                        .load(url)
                        .into(ivStory)
                } else {
                    Glide.with(requireActivity())
                        .load(url)
                        .into(ivStory)
                }
            }


            if (!reelData?.caption.isNullOrEmpty() && reelData?.caption != "null") {
                tvTitle.isVisible = true
                tvTitle.text = reelData?.caption
            } else {
                tvTitle.isVisible = false
            }

            videoStory.setOnCompletionListener {

                val duration = videoStory.duration

                seekBar.progress = duration
                tvCurrentTime.text = formatTime(duration)
               // tvRemainingTime.text = "-00:00"

                ivPlay.isVisible = true
                ivPlay.setImageResource(R.drawable.ic_play)

                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivLike.setOnClickListener {
                addRemoveReelToWishlist()
            }

            ivShare.setOnClickListener {
                shareReel()
            }

            ivPlay.setOnClickListener {
                if (videoStory.isPlaying) {
                    videoStory.pause()
                    ivPlay.setImageResource(R.drawable.ic_play)
                } else {

                    if (videoStory.currentPosition >= videoStory.duration - 500) {
                        videoStory.seekTo(0)

                        seekBar.progress = 0
                        tvCurrentTime.text = "00:00"
                   //     tvRemainingTime.text = "-${formatTime(videoStory.duration)}"
                        tvRemainingTime.text = "${formatTime(videoStory.duration)}"
                    }

                    videoStory.start()
                    ivPlay.setImageResource(R.drawable.ic_pause)

                    startSeekBarUpdate()
                }
            }

            seekBar.setOnSeekBarChangeListener(object :
                android.widget.SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        videoStory.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            })
        }
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                binding?.videoLoader?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false

            }
        }

        viewModel.addRemoveReelToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (reelData?.wishlist==1){
                    reelData?.wishlist = 0
                    showToast("Reel removed from your wishlist successfully")
                }
                else{
                    reelData?.wishlist = 1
                    showToast("Reel added to your wishlist successfully")
                }
                setReelLikeStatus()
                isObserver = false
            }

        }

        viewModel.shareReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.videoLoader?.isVisible = false
                val fileUrl = reelData?.file ?: ""

                val isVideo = fileUrl.contains(".mp4", true) ||
                        fileUrl.contains(".mov", true) ||
                        fileUrl.contains(".mkv", true) ||fileUrl.contains(".avi", true)
                CommonMethod.shareReel(requireActivity(),
                    caption = reelData?.caption ?: "",
                    mediaUrl = fileUrl,
                    link = response.data.file ?: "",
                    isVideo = isVideo

                )
                isObserver = false
            }
        }
    }

    private fun addRemoveReelToWishlist(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelData?.id.toString()
        viewModel.addRemoveReelToWishlist(map)
        isObserver = true
    }
    private fun shareReel(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelData?.id.toString()
        viewModel.shareReel(map)
        isObserver = true
        binding?.videoLoader?.isVisible = true
    }

    private fun setReelLikeStatus(){
        binding?.apply {
            if (reelData?.wishlist == 1) {
                ivLike.setImageResource(R.drawable.ic_like_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like)
            }
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun startSeekBarUpdate() {
        runnable = object : Runnable {
            override fun run() {
                val currentPosition = binding?.videoStory?.currentPosition ?: 0
                val duration = binding?.videoStory?.duration ?: 0

                if (currentPosition >= duration - 500) {
                    binding?.seekBar?.progress = duration
                    binding?.tvCurrentTime?.text = formatTime(duration)
                  //  binding?.tvRemainingTime?.text = "-00:00"
                    binding?.tvRemainingTime?.text = "${formatTime(duration)}"
                } else {
                    binding?.seekBar?.progress = currentPosition
                    binding?.tvCurrentTime?.text = formatTime(currentPosition)
                  //  binding?.tvRemainingTime?.text = "-${formatTime(duration - currentPosition)}"
                    binding?.tvRemainingTime?.text = "${formatTime(duration)}"
                }

                handler.postDelayed(this, 500)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        binding = null
    }
}
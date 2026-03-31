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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewReelFragment : BaseFragment() {
    private var binding: FragmentViewReelBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var reelData: GetSellerStoryResponse.Data?=null
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


            val url = reelData?.file ?: ""

            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov")
            val isGif = url.endsWith(".gif")

            if (isVideo) {
                videoLoader.isVisible = true
                ivStory.isVisible = false
                videoStory.isVisible = true

                videoStory.setVideoPath(url)

                videoStory.setOnPreparedListener { mp ->
                    mp.isLooping = false
                   videoLoader.isVisible = false

                    videoStory.start()
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


            if (!reelData?.title.isNullOrEmpty() && reelData?.title != "null") {
                tvTitle.isVisible = true
                tvTitle.text = reelData?.title
            } else {
                tvTitle.isVisible = false
            }

            videoStory.setOnCompletionListener {
                ivPlay.setImageResource(R.drawable.ic_play)

            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivPlay.setOnClickListener {
                if (videoStory.isPlaying) {
                    videoStory.pause()
                    ivPlay.isVisible = true
                } else {
                    videoStory.start()
                    ivPlay.setImageResource(R.drawable.ic_pause)
                }

            }
        }
    }

    override fun observe() {
    }
}
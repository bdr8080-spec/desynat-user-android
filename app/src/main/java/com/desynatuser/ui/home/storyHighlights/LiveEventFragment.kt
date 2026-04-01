package com.desynatuser.ui.home.storyHighlights

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.utils.agora.AgoraManager
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentLiveEventBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import kotlin.getValue

@AndroidEntryPoint
class LiveEventFragment : BaseFragment() {
    private var binding: FragmentLiveEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveEventBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        AgoraManager.initialize(
            requireContext(),
            getString(R.string.agora_app_id),
            rtcEventHandler
        )

        joinChannel()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {
    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Audience joined channel")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Host joined: $uid")
            binding?.tvNoDataFound?.isVisible = false
            activity?.runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("LIVE_TEST", "Host left")
            binding?.tvNoDataFound?.isVisible = true

            activity?.runOnUiThread {
                binding?.videoContainer?.removeAllViews()
            }
        }

        override fun onError(err: Int) {
            Log.e("LIVE_TEST", "Agora Error: $err")
            binding?.tvNoDataFound?.isVisible = true
        }
    }

    private fun joinChannel() {

        val engine = AgoraManager.getEngine()

        engine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        engine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        engine?.enableVideo()

        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_AUDIENCE
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }

        engine?.joinChannel(
            getString(R.string.agora_token),
            "liveChannel123",
            0,
            options
        )
    }

    private fun setupRemoteVideo(uid: Int) {

        binding?.videoContainer?.removeAllViews()

        val surfaceView = RtcEngine.CreateRendererView(requireContext())

        binding?.videoContainer?.addView(surfaceView)

        AgoraManager.getEngine()?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        AgoraManager.getEngine()?.leaveChannel()

        binding = null
    }

}
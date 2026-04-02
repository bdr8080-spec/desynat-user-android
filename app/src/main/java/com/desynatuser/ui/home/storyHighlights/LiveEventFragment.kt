package com.desynatuser.ui.home.storyHighlights

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.utils.agora.AgoraManager
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentLiveEventBinding
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.vision.VisionRetrofit
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class LiveEventFragment : BaseFragment() {
    private var binding: FragmentLiveEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isTokenObserver = true
    private var channelId = ""
    private var token = ""
    private var uid = 0
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
        getLiveStreaming()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getLiveStreamingRoomResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.data!=null){
                    if (response.data?.currentRequests?.isNotEmpty() == true){

                        channelId = response?.data?.currentRequests?.get(0)?.channelId.toString()

                        if (response?.data?.currentRequests?.get(0)?.liveToken==null){
                            getAgoraLiveTokenApi(response.data.currentRequests[0].id.toString())
                        }
                        else{
                            token = response?.data?.currentRequests?.get(0)?.liveToken.toString()
                            val uidValue = response?.data?.currentRequests?.get(0)?.uid.toString()
                            uid = uidValue.toIntOrNull() ?: 0
                            startLiveStreaming()
                        }

                    }
                    else  if (response.data?.futureRequests?.isNotEmpty() == true){
                        showNoDataUIForNextEvent(true)

                    }
                    else{
                        showNoDataUI(true)
                    }
                }
                else{
                    showNoDataUI(true)
                }

                isObserver = false
            }
        }

        viewModel.getAgoraTokenResult.observe(viewLifecycleOwner) { response ->
            if (isTokenObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.token!=null){
                        token = response.token
                    Log.d("LIVE_TEST", "token ${token}")

                }

                isTokenObserver = false
            }
        }

    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Audience joined channel")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Host joined: $uid")
            activity?.runOnUiThread {
                setupRemoteVideo(uid)

                showNoDataUIWaiting(false)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("LIVE_TEST", "Host left")
            activity?.runOnUiThread {

                binding?.videoContainer?.removeAllViews()

                AgoraManager.getEngine()?.setupRemoteVideo(
                    VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid)
                )

                showNoDataUI(true)
            }

            checkForNextEvent()
        }

        override fun onError(err: Int) {
            Log.e("LIVE_TEST", "Agora Error: $err")
            activity?.runOnUiThread {
                showNoDataUI(true)
            }
        }
    }

    private fun showNoDataUI(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
        }
    }

    private fun showNoDataUIForNextEvent(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvNoDataFound.text = "Waiting for next live event..."
        }
    }

    private fun showNoDataUIWaiting(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvNoDataFound.text = "Waiting for host to join"
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
               token,
            channelId,
            uid,
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

    private fun getLiveStreaming(){
        viewModel.getLiveStreamingRoom()
        isObserver = true
    }

    private fun getAgoraToken(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        viewModel.getAgoraToken(SharedPreference.get(requireActivity()).accessToken,map)
        isTokenObserver = true
    }

    private fun checkForNextEvent() {

        Handler(Looper.getMainLooper()).postDelayed({

            Log.d("LIVE_TEST", "Checking for next event...")

            getLiveStreaming()

        }, 5000)
    }

    private fun startLiveStreaming(){

        AgoraManager.initialize(
            requireContext(),
            getString(R.string.agora_app_id),
            rtcEventHandler
        )

        joinChannel()
    }

    private fun getAgoraLiveTokenApi(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        lifecycleScope.launch {
            try {
                val response = VisionRetrofit.api.getAgoraToken(
                    SharedPreference.get(requireActivity()).accessToken,
                    map
                )
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("AGORA_TOKEN_RESPONSE", response.body().toString() ?: "Error")

                token = response.body()?.token.toString()
                val uidValue = response.body()?.uid.toString()
                uid = uidValue.toIntOrNull() ?: 0
                startLiveStreaming()

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("AGORA_TOKEN_ERROR", e.message ?: "Error")
            }
        }
    }

}
package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatuser.adapter.HomeFullScreenBannerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentFullScreenBannerBinding
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenBannerFragment : BaseFragment() {

    private var binding: FragmentFullScreenBannerBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private var isHomeBannerObserver = false
    private var homeFullScreenBannerAdapter: HomeFullScreenBannerAdapter? = null

    private val imageList = ArrayList<String>()
    private val handler = Handler(Looper.getMainLooper())

    private var lastStartedPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenBannerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        getHomePopScreenBanner()

        binding?.apply {
            homeFullScreenBannerAdapter =
                HomeFullScreenBannerAdapter(
                    requireActivity(),
                    imageList,
                    object : HomeFullScreenBannerAdapter.OnClick {
                        override fun onClick(position: Int) {}
                    }
                ) {
                    startTimerForCurrentItem()
                }

            viewPager.adapter = homeFullScreenBannerAdapter
            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()
        }
    }

    override fun initControl() {
        binding?.tvSkip?.setOnClickListener {
            stopTimers()
            navigateNextScreen()
        }
    }

    private fun getHomePopScreenBanner() {
        viewModel.getHomeScreenBanner()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isHomeBannerObserver = true
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isHomeBannerObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let {
                    showToast(it)
                    navigateNextScreen()
                }
                isHomeBannerObserver = false
            }
        }

        viewModel.getHomeScreenBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeBannerObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                response?.let {
                    imageList.clear()
                    imageList.addAll(it.data.map { banner -> banner.image })
                    homeFullScreenBannerAdapter?.notifyDataSetChanged()

                    if (imageList.isEmpty()) {
                        navigateNextScreen()
                    } else {
                        binding?.relWhite?.isVisible = false
                    }
                }
                isHomeBannerObserver = false
            }
        }
    }

    private val slideRunnable = object : Runnable {
        override fun run() {
            if (!isAdded) return

            binding?.viewPager?.let { vp ->
                val next = vp.currentItem + 1
                if (next < imageList.size) {
                    vp.currentItem = next
                    handler.postDelayed(this, 4000)
                } else {
                    navigateNextScreen()
                }
            }
        }
    }

    private fun startTimerForCurrentItem() {
        val currentPosition = binding?.viewPager?.currentItem ?: return

        if (currentPosition == lastStartedPosition) return
        lastStartedPosition = currentPosition

        stopTimers()

        if (imageList.size == 1) {
            handler.postDelayed({
                navigateNextScreen()
            }, 4000)
        } else if (imageList.size > 1) {
            handler.postDelayed(slideRunnable, 4000)
        }
    }

    private fun stopTimers() {
        handler.removeCallbacks(slideRunnable)
        handler.removeCallbacksAndMessages(null)
    }

    private fun navigateNextScreen() {
        if (!isAdded) return

        stopTimers()

        activity?.let {
            it.startActivity(Intent(it, HomeActivity::class.java))
            it.finish()
        }
    }

    override fun onStop() {
        stopTimers()
        super.onStop()
    }

    override fun onDestroyView() {
        stopTimers()
        binding = null
        super.onDestroyView()
    }
}

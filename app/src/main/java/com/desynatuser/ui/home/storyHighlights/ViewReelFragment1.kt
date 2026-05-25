package com.desynatuser.ui.home.storyHighlights

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.adapter.ViewReelAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.Fragment1ViewReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlin.getValue
import kotlin.text.compareTo
import kotlin.text.toFloat

@AndroidEntryPoint
class ViewReelFragment1 : BaseFragment() {
    private var binding: Fragment1ViewReelBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var reelData: GetSellerStoryResponse.Data?=null

    private val handler = Handler()
    private var runnable: Runnable? = null
    private var viewReelAdapter: ViewReelAdapter?=null
    private var reelList = ArrayList<GetSellerStoryResponse.Data>()
    var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = Fragment1ViewReelBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        val json = arguments?.getString("storyList")
        selectedPos = arguments?.getInt("position")?:-1
        val type = object : TypeToken<ArrayList<GetSellerStoryResponse.Data>>() {}.type

        reelList = Gson().fromJson(json, type)

        binding?.apply {

            rvReel?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvReel)

            viewReelAdapter = ViewReelAdapter(
                requireContext(),
                reelList,
                object : ViewReelAdapter.OnClick {

                    override fun onItemClick(position: Int, type: Int) {

                        selectedPos = position
                        reelData = reelList[position]

                        when(type) {

                            1 -> {
                                addRemoveReelToWishlist()
                            }

                            2 -> {
                                shareReel()
                            }
                        }
                    }
                })

            rvReel?.adapter = viewReelAdapter

            if (selectedPos >= 0) {

                rvReel?.post {

                    (rvReel?.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(selectedPos, 0)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

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
                    showToast(getString(R.string.reel_removed_from_your_wishlist_successfully))
                }
                else{
                    reelData?.wishlist = 1
                    showToast(getString(R.string.reel_added_to_your_wishlist_successfully))
                }
                reelList[selectedPos].wishlist = reelData?.wishlist?:0
                viewReelAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1

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
                selectedPos = -1
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

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)
    }

}
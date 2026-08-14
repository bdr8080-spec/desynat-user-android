package com.desynatuser.ui.home.storyHighlights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.desynatuser.R
import com.desynatuser.adapter.ViewReelAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentViewReelBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewReelFragment : BaseFragment() {
    private var binding: FragmentViewReelBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var reelData: GetSellerStoryResponse.Data? = null

    private var viewReelAdapter: ViewReelAdapter? = null
    private var reelList = ArrayList<GetSellerStoryResponse.Data>()
    var selectedPos = -1

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
        // Support both single item (storyData) and list (storyList) for compatibility
        val singleJson = arguments?.getString("storyData")
        val listJson = arguments?.getString("storyList")
        selectedPos = arguments?.getInt("position") ?: 0

        if (!listJson.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<GetSellerStoryResponse.Data>>() {}.type
            reelList = Gson().fromJson(listJson, type)
        } else if (!singleJson.isNullOrEmpty()) {
            val type = object : TypeToken<GetSellerStoryResponse.Data>() {}.type
            val singleItem: GetSellerStoryResponse.Data = Gson().fromJson(singleJson, type)
            reelList = arrayListOf(singleItem)
            selectedPos = 0
        }

        binding?.apply {
            rvReel.layoutManager =
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
                        when (type) {
                            1 -> addRemoveReelToWishlist()
                            2 -> shareReel()
                        }
                    }
                })

            rvReel.adapter = viewReelAdapter

            if (selectedPos >= 0 && selectedPos < reelList.size) {
                rvReel.post {
                    (rvReel.layoutManager as LinearLayoutManager)
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
                val currentReel = reelList.getOrNull(selectedPos)
                if (currentReel != null) {
                    if (currentReel.wishlist == 1) {
                        currentReel.wishlist = 0
                        showToast(getString(R.string.reel_removed_from_your_wishlist_successfully))
                    } else {
                        currentReel.wishlist = 1
                        showToast(getString(R.string.reel_added_to_your_wishlist_successfully))
                    }
                    viewReelAdapter?.notifyItemChanged(selectedPos)
                }
                isObserver = false
            }
        }

        viewModel.shareReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.videoLoader?.isVisible = false
                val currentReel = reelList.getOrNull(selectedPos)
                val fileUrl = currentReel?.file ?: ""

                val isVideo = fileUrl.contains(".mp4", true) ||
                        fileUrl.contains(".mov", true) ||
                        fileUrl.contains(".mkv", true) || fileUrl.contains(".avi", true)
                CommonMethod.shareReel(
                    requireActivity(),
                    caption = currentReel?.caption ?: "",
                    mediaUrl = fileUrl,
                    link = response.data.file ?: "",
                    isVideo = isVideo
                )
                isObserver = false
            }
        }
    }

    private fun addRemoveReelToWishlist() {
        val currentReel = reelList.getOrNull(selectedPos) ?: return
        val map = HashMap<String, Any>()
        map["reel_id"] = currentReel.id.toString()
        viewModel.addRemoveReelToWishlist(map)
        isObserver = true
    }

    private fun shareReel() {
        val currentReel = reelList.getOrNull(selectedPos) ?: return
        val map = HashMap<String, Any>()
        map["reel_id"] = currentReel.id.toString()
        viewModel.shareReel(map)
        isObserver = true
        binding?.videoLoader?.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
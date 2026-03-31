package com.desynatuser.ui.home.storyHighlights

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.desynatuser.R
import com.desynatuser.adapter.StoryPagerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentViewStoryBinding
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import me.leolin.shortcutbadger.ShortcutBadger
import kotlin.getValue

@AndroidEntryPoint
class ViewStoryFragment : BaseFragment() {
    private var binding : FragmentViewStoryBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private val myStories = ArrayList<GetSellerStoryResponse.Data>()

    private val progressBars = ArrayList<ProgressBar>()
    private var storyPagerAdapter: StoryPagerAdapter?=null
    private var from = "admin"
    private var sellerId: String=""

    private var progressAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewStoryBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {


        from = arguments?.getString("from").toString()
        val json = arguments?.getString("storyData")

        if (!json.isNullOrEmpty()) {

            val type = object : TypeToken<ArrayList<GetSellerStoryResponse.Data>>() {}.type

            val storyList: ArrayList<GetSellerStoryResponse.Data> =
                Gson().fromJson(json, type)

            myStories.clear()
            myStories.addAll(storyList)
        }
        if (from!="admin"){
            sellerId = arguments?.getString("sellerId").toString()
        }
//        when(from){
//            "admin"->getAdminStory()
//            "reel"-> getSellerReel()
//            "story"->getSellerStory()
//            "highlights"->getSellerHighlights()
//        }

        storyPagerAdapter = StoryPagerAdapter(
            myStories,
            onVideoReady = { position, duration ->
                startVideoProgress(position, duration)
            },
            onVideoFinished = { position ->
                val next = position + 1
                if (next < myStories.size) {
                    binding?.viewPager?.currentItem = next
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        )
        binding?.viewPager?.adapter = storyPagerAdapter

        setData()

        binding?.viewPager?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (progressBars.isEmpty()) return

                    // cancel old animation safely
                    progressAnimator?.removeAllListeners()
                    progressAnimator?.cancel()

                    progressBars.forEachIndexed { index, progressBar ->
                        progressBar.progress = if (index < position) 100 else 0
                    }

                    startProgress(position)
                }
            }
        )


    }

    override fun initControl() {
        binding?.apply {
            ivCancel.setOnClickListener {
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

        viewModel.getAdminStoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    storyPagerAdapter?.notifyDataSetChanged()
                    setData()
                }
                isObserver = false
            }
        }

        viewModel.getSellerHighlightsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    storyPagerAdapter?.notifyDataSetChanged()

                    setData()
                }
                isObserver = false
            }
        }

        viewModel.getSellerReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    storyPagerAdapter?.notifyDataSetChanged()
                    setData()
                }
                isObserver = false
            }
        }

        viewModel.getSellerStoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    storyPagerAdapter?.notifyDataSetChanged()
                    setData()
                }
                isObserver = false
            }
        }

    }

    private fun setupProgressBars(count: Int) {

        binding?.layoutProgress?.removeAllViews()
        progressBars.clear()

        repeat(count) { index ->

            val progressBar = ProgressBar(
                requireContext(),
                null,
                android.R.attr.progressBarStyleHorizontal
            )

            progressBar.progressDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.story_progress)

            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            params.weight = 1f
            params.marginEnd = 8

            progressBar.layoutParams = params
            progressBar.max = 100

            binding?.layoutProgress?.addView(progressBar)

            progressBars.add(progressBar)
        }
    }


    private fun startProgress(index: Int) {

        if (progressBars.isEmpty() || index >= progressBars.size) return

        val story = myStories[index]
        val url = story.file ?: ""



        if (url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".gif")) {
            return
        }

        val duration = 10000L

        progressAnimator?.cancel()

        if (duration > 0) {

            progressAnimator = ValueAnimator.ofInt(0, 100)
            progressAnimator?.duration = duration

            progressAnimator?.addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            progressAnimator?.doOnEnd {

                if (!isAdded) return@doOnEnd

                val nextIndex = index + 1

                if (nextIndex < progressBars.size) {
                    binding?.viewPager?.currentItem = nextIndex
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            progressAnimator?.start()
        }
    }

    private fun getAdminStory(){
        val map = HashMap<String, Any>()
        viewModel.getAdminStory(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSellerStory(){
        val map = HashMap<String, Any>()
        map["seller_id"]= sellerId
        viewModel.getSellerStory(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSellerReel(){
        val map = HashMap<String, Any>()
        map["seller_id"]= sellerId
        viewModel.getSellerReel(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSellerHighlights(){
        val map = HashMap<String, Any>()
        map["seller_id"]= sellerId
        viewModel.getSellerHighlights(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setData(){
        if (myStories.isNotEmpty()) {
            setupProgressBars(myStories.size)
            startProgress(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimator?.cancel()
        binding = null
    }

    private fun startVideoProgress(index: Int, duration: Long) {

        progressAnimator?.cancel()

        progressAnimator = ValueAnimator.ofInt(0, 100)
        progressAnimator?.duration = duration

        progressAnimator?.addUpdateListener {
            progressBars[index].progress = it.animatedValue as Int
        }

        progressAnimator?.doOnEnd {

            if (!isAdded) return@doOnEnd

            val nextIndex = index + 1

            if (nextIndex < progressBars.size) {
                binding?.viewPager?.currentItem = nextIndex
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        progressAnimator?.start()
    }
}
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
import androidx.recyclerview.widget.RecyclerView
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
import kotlin.collections.get
import kotlin.compareTo
import kotlin.getValue
import kotlin.toString

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

    private var isManualNavigation = false

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

        setupAdapter()
        setupPagerCallback()
        setData()


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

    private fun setData() {

        if (myStories.isNotEmpty()) {

            setupProgressBars(myStories.size)

            val startPosition = myStories.indexOfFirst { it.isSeen == 0 }
                .takeIf { it != -1 } ?: 0

            binding?.viewPager?.setCurrentItem(startPosition, false)

            val story = myStories[startPosition]

            if ((from == "story" || from == "admin") && story.isSeen == 0) {
                seeStory(story.id.toString())
                story.isSeen = 1
            }

            progressBars.forEachIndexed { index, progressBar ->
                progressBar.progress = if (index < startPosition) 100 else 0
            }

            startProgress(startPosition)
        }
    }

    private fun setupAdapter() {

        storyPagerAdapter = StoryPagerAdapter(requireActivity(),
            myStories,
            object : StoryPagerAdapter.OnClick {

                override fun clickOnItem(position: Int) {

                }


                override fun onNext(position: Int) {
                    isManualNavigation = true

                    stopProgress()

                    val current = binding?.viewPager?.currentItem ?: return
                    val next = current + 1

                    if (next < myStories.size) {
                        binding?.viewPager?.currentItem = next
                    } else {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

                override fun onPrevious(position: Int) {
                    isManualNavigation = true

                    stopProgress()

                    val current = binding?.viewPager?.currentItem ?: return
                    val prev = current - 1

                    if (prev >= 0) {
                        binding?.viewPager?.currentItem = prev
                    }
                }

                override fun onPause(position: Int) {
                    progressAnimator?.pause()
                }

                override fun onResume(position: Int) {
                    progressAnimator?.resume()
                }
            },

            onVideoReady = { position, duration ->
                startVideoProgress(position, duration)
            },

            onVideoFinished = { position ->

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@StoryPagerAdapter
                }

                goToNext(position)
            }
        )

        binding?.viewPager?.adapter = storyPagerAdapter
    }

    private fun setupPagerCallback() {
        binding?.viewPager?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    stopProgress()

                    progressBars.forEachIndexed { index, progressBar ->
                        progressBar.progress = if (index < position) 100 else 0
                    }

                    val story = myStories[position]

                    if ((from == "story" || from == "admin") && story.isSeen == 0) {
                        seeStory(story.id.toString())
                        story.isSeen = 1
                    }

                    startProgress(position)
                }
            }
        )
    }


    private fun stopProgress() {
        progressAnimator?.removeAllListeners()
        progressAnimator?.cancel()
    }

    private fun startProgress(index: Int) {

        if (progressBars.isEmpty() || index >= progressBars.size) return

        val url = myStories[index].file ?: ""

        // Skip video/gif
        if (url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".gif")) {
            return
        }

        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L

            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            doOnEnd {

                if (!isAdded) return@doOnEnd

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@doOnEnd
                }

                goToNext(index)
            }

            start()
        }
    }

    private fun startVideoProgress(index: Int, duration: Long) {

        stopProgress()

        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
         //   this.duration = duration

            val safeDuration = if (duration > 0) duration else 10000L

            this.duration = safeDuration

            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            doOnEnd {

                if (!isAdded) return@doOnEnd

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@doOnEnd
                }

                goToNext(index)
            }

            start()
        }
    }


    private fun goToNext(position: Int) {
        val next = position + 1

        if (next < myStories.size) {
            binding?.viewPager?.currentItem = next
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupProgressBars(count: Int) {

        binding?.layoutProgress?.removeAllViews()
        progressBars.clear()

        repeat(count) {

            val progressBar = ProgressBar(
                requireContext(),
                null,
                android.R.attr.progressBarStyleHorizontal
            )

            progressBar.progressDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.story_progress)

            progressBar.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginEnd = 8
            }

            progressBar.max = 100

            binding?.layoutProgress?.addView(progressBar)
            progressBars.add(progressBar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopProgress()
        binding = null
    }

    private fun seeStory(id: String){
        val map = HashMap<String, Any>()
        map["story_id"] = id
        map["type"] = 1
        map["user_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
        viewModel.seeStory(map)
    }
}
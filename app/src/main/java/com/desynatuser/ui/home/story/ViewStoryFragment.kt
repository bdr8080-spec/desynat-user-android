package com.desynatuser.ui.home.story

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
import androidx.viewpager2.widget.ViewPager2
import com.desynatuser.R
import com.desynatuser.adapter.StoryPagerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentViewStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class ViewStoryFragment : BaseFragment() {
    private var binding : FragmentViewStoryBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private val myStories = ArrayList<String>()

    private val progressBars = ArrayList<ProgressBar>()

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

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

        val myStories = ArrayList<String>()

        myStories.add("https://picsum.photos/600/800")

        myStories.add("https://picsum.photos/600/801")


        setupProgressBars(myStories.size)
        binding?.viewPager?.adapter = StoryPagerAdapter(myStories)

        binding?.viewPager?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    progressBars.forEachIndexed { index, progressBar ->
                        progressBar.progress = if (index < position) 100 else 0
                    }

                    startProgress(position)
                }
            }
        )

        startProgress(0)

    }

    override fun initControl() {
    }

    override fun observe() {
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

        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 10000

        animator.addUpdateListener {
            progressBars[index].progress = it.animatedValue as Int
        }

        animator.doOnEnd {

            if (!isAdded) return@doOnEnd   // prevent crash

            if (index < progressBars.size - 1) {
                binding?.viewPager?.currentItem = index + 1
            } else {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        animator.start()
    }

}
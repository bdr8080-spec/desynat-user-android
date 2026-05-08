package com.desynatuser.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.BannerViewPagerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.databinding.FragmentOnBoardingBinding
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment() {
    private var binding: FragmentOnBoardingBinding? = null
    private var bannerAdapter : BannerViewPagerAdapter? =null
    private var bannerList = ArrayList<BannerData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        bannerList.clear()

        if (SharedPreference.get(requireActivity()).userType==2){
            bannerList.add(BannerData(R.drawable.order_item,
                getString(R.string.earn_money),
                getString(R.string.lorem_ipsum)))
            bannerList.add(BannerData(R.drawable.product_delivery,
                getString(R.string.track_commission),getString(R.string.lorem_ipsum)))
            bannerList.add(BannerData(R.drawable.item_tracking,
                getString(R.string.share_product_link),getString(R.string.lorem_ipsum)))

        }
        else{
            bannerList.add(BannerData(R.drawable.order_item,
                getString(R.string.order_item),getString(R.string.lorem_ipsum)))
            bannerList.add(BannerData(R.drawable.product_delivery,
                getString(R.string.product_delivery),getString(R.string.lorem_ipsum)))
            bannerList.add(BannerData(R.drawable.item_tracking,
                getString(R.string.item_tracking),getString(R.string.lorem_ipsum)))
        }
       bannerAdapter = BannerViewPagerAdapter(bannerList)
        binding?.viewPager?.adapter = bannerAdapter
        binding?.dotsIndicator?.attachTo(binding?.viewPager!!)

        binding?.viewPager?.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        updateUI(0)
        
    }

    private fun updateUI(position: Int) {
        val totalPages = bannerList.size
        when (position) {
            0 -> {
                binding?.icBackArrow?.visibility = View.INVISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.VISIBLE
            }
            totalPages - 1 -> {
                binding?.icBackArrow?.visibility = View.VISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.GONE
            }
            else -> {
                binding?.icBackArrow?.visibility = View.VISIBLE
                binding?.icForwardArrow?.visibility = View.VISIBLE
                binding?.tvSkip?.visibility = View.VISIBLE
            }
        }
    }

    override fun initControl() {
        binding?.icForwardArrow?.setOnClickListener {
            val current = binding?.viewPager?.currentItem ?: 0
            if (current < bannerList.size - 1) {
                binding?.viewPager?.currentItem = current + 1
            } else {
                if (SharedPreference.get(requireActivity()).userType==2){
                    if (SharedPreference.get(requireActivity())
                            .getUser()?.affiliatedUserdetails?.adminVerify != null
                    ) {
                        findNavController().navigate(
                            R.id.action_onBoardingFragment_to_LoginFragment,
                            null,
                            NavOptions.Builder()
                                .setPopUpTo(findNavController().graph.startDestinationId, true)
                                .build()
                        )
                    }
                    else{
                        findNavController().navigate(
                            R.id.action_onBoardingFragment_to_affiliateSignUpFragment,
                            null,
                            NavOptions.Builder()
                                .setPopUpTo(findNavController().graph.startDestinationId, true)
                                .build()
                        )
                    }


                }
                else {
                    SharedPreference.get(requireActivity()).isTutorial = true
                    findNavController().navigate(
                        R.id.action_onBoardingFragment_to_LoginFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().graph.startDestinationId, true)
                            .build()
                    )
                }
            }
        }

        binding?.icBackArrow?.setOnClickListener {
            val current = binding?.viewPager?.currentItem ?: 0
            if (current > 0) {
                binding?.viewPager?.currentItem = current - 1
            }
        }

        binding?.tvSkip?.setOnClickListener {
            if (SharedPreference.get(requireActivity()).userType==2){
                if (SharedPreference.get(requireActivity())
                        .getUser()?.affiliatedUserdetails?.adminVerify != null
                ) {
                    findNavController().navigate(
                        R.id.action_onBoardingFragment_to_LoginFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().graph.startDestinationId, true)
                            .build()
                    )
                }
                else{
                    findNavController().navigate(
                        R.id.action_onBoardingFragment_to_affiliateSignUpFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().graph.startDestinationId, true)
                            .build()
                    )
                }
            }

            else{
                SharedPreference.get(requireActivity()).isTutorial = true
                findNavController().navigate(
                    R.id.action_onBoardingFragment_to_LoginFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(findNavController().graph.startDestinationId, true)
                        .build()
                )

            }

        }

    }

    override fun observe() {
    }
}

data class BannerData(
    var image: Int,
    var title:String,
    var description:String
)

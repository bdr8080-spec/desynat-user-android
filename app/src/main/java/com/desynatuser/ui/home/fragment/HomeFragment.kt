package com.desynatuser.ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.desynatuser.R
import com.desynatuser.adapter.ViewPagerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private var adapter: ViewPagerAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            adapter = ViewPagerAdapter(this@HomeFragment)
            viewPager.adapter = adapter

            viewPager.isUserInputEnabled = false

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Men"
                    1 -> tab.text = "Women"
                    2 -> tab.text = "Kids"
                }
            }.attach()

            viewPager.post {
                viewPager.currentItem = 1
            }
        }
    }

    override fun initControl() {
    }

    override fun observe() {
    }


}
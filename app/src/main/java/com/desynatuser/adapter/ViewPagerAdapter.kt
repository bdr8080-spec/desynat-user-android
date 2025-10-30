package com.desynatuser.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.desynatuser.ui.home.fragment.KidsFragment
import com.desynatuser.ui.home.fragment.MenFragment
import com.desynatuser.ui.home.fragment.WomenFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        MenFragment(),
        WomenFragment(),
        KidsFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}

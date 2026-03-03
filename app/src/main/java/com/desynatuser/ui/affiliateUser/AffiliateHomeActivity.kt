package com.desynatuser.ui.affiliateUser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatuser.R
import com.desynatuser.adapter.BottomNavAdapter
import com.desynatuser.base.BaseActivity
import com.desynatuser.databinding.ActivityAffiliateHomeBinding
import com.desynatuser.ui.response.BottomNavItem
import com.desynatuser.utils.CommonMethod
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateHomeActivity : BaseActivity() {
    private var binding: ActivityAffiliateHomeBinding? = null
    private lateinit var controller: NavController
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter: BottomNavAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAffiliateHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        CommonMethod.detectGestureMode(binding?.root!!, binding?.viewBottom!!)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val imeInset = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            val reduceBy = resources.getDimensionPixelSize(R.dimen.keyboard_padding_reduce)

            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                imeInset
            )
            insets
        }

        MFSDK.setUpActionBar(isShowToolBar = false)
        initView()
        initControl()
        observer()
    }

    override fun initView() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.affiliate_home_navigation_graph)
        controller.graph = navGraph

        bottonNavItemList.clear()
        bottonNavItemList.add(
            BottomNavItem(
                "Home",
                R.drawable.ic_home,
                R.drawable.ic_selected_home
            )
        )
        bottonNavItemList.add(
            BottomNavItem(
                "Products",
                R.drawable.products_ic,
                R.drawable.ic_selected_products
            )
        )
        bottonNavItemList.add(
            BottomNavItem(
                "Category",
                R.drawable.icon_affiliate,
                R.drawable.icon_affiliate
            )
        )
        bottonNavItemList.add(
            BottomNavItem(
                "Payments",
                R.drawable.payments_ic,
                R.drawable.ic_selected_payment
            )
        )
        bottonNavItemList.add(
            BottomNavItem(
                "Profile",
                R.drawable.ic_profile,
                R.drawable.ic_selected_profile
            )
        )

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.affiliateHome,
            R.id.affiliateProfile,
            R.id.affiliateCategory,
            R.id.affiliateMyProduct,
            R.id.affiliatePaymentFragment
        )

        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations) {
                binding?.rvBottomNav?.isVisible = true
                binding?.viewBackground?.isVisible = true
            } else {
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = false
            }

            when (destination.id) {
                R.id.affiliateHome -> {
                    bottomAdapter?.setSelectedItem(0)
                }

                R.id.affiliateMyProduct -> {
                    bottomAdapter?.setSelectedItem(1)
                }

                R.id.affiliateCategory -> {
                    bottomAdapter?.setSelectedItem(2)
                }

                R.id.affiliatePaymentFragment -> {
                    bottomAdapter?.setSelectedItem(3)
                }

                R.id.affiliateProfile -> {
                    bottomAdapter?.setSelectedItem(4)
                }
            }
        }

    }

    override fun initControl() {
    }

    override fun observer() {
    }

    fun setBottomAdapter() {
        bottomAdapter =
            BottomNavAdapter(this, bottonNavItemList, object : BottomNavAdapter.BottomNavItemClick {
                override fun onBottomNavItemClick(position: Int) {
                    when (position) {
                        0 -> controller.navigate(R.id.affiliateHome)
                        1 -> controller.navigate(R.id.affiliateMyProduct)
                        2 -> controller.navigate(R.id.affiliateCategory)
                        3 -> controller.navigate(R.id.affiliatePaymentFragment)
                        4 -> controller.navigate(R.id.affiliateProfile)
                    }

                }


            })
        binding?.rvBottomNav?.adapter = bottomAdapter

    }

}

package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatuser.R
import com.desynatuser.adapter.BestSellingAdapter
import com.desynatuser.adapter.CategoryAdapter
import com.desynatuser.adapter.DiscountedDealBannerViewPagerAdapter
import com.desynatuser.adapter.HomeWomenBannerViewPagerAdapter
import com.desynatuser.adapter.NewestProductAdapter
import com.desynatuser.adapter.PremiumStoreBannerViewPagerAdapter
import com.desynatuser.adapter.SuggestedStoreAdapter
import com.desynatuser.adapter.TopRatedAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentWomenBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WomenFragment :  BaseFragment() {
    private var binding: FragmentWomenBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var bestSellingAdapter: BestSellingAdapter?=null

    private var categoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var categoryAdapter: CategoryAdapter?=null
    private var newestProductAdapter: NewestProductAdapter?=null
    private var suggestedStoreAdapter: SuggestedStoreAdapter?=null
    private var topRatedAdapter: TopRatedAdapter?=null

    private var homeWomenBannerViewPagerAdapter : HomeWomenBannerViewPagerAdapter? =null
    private var discountedDealBannerViewPagerAdapter : DiscountedDealBannerViewPagerAdapter? =null
    private var premiumStoreBannerViewPagerAdapter : PremiumStoreBannerViewPagerAdapter? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWomenBinding.inflate(layoutInflater,container,false)
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

            homeWomenBannerViewPagerAdapter = HomeWomenBannerViewPagerAdapter(listOf(R.drawable.banner,R.drawable.premium,R.drawable.deal,R.drawable.banner))
            viewPager.adapter = homeWomenBannerViewPagerAdapter
            dotsIndicator.attachTo(binding?.viewPager!!)

            discountedDealBannerViewPagerAdapter = DiscountedDealBannerViewPagerAdapter(listOf(R.drawable.deal,R.drawable.banner,R.drawable.premium,R.drawable.deal))
            viewPagerDiscountedDeals.adapter = discountedDealBannerViewPagerAdapter
            dotsIndicatorDiscountedDeals.attachTo(binding?.viewPagerDiscountedDeals!!)

            premiumStoreBannerViewPagerAdapter = PremiumStoreBannerViewPagerAdapter(listOf(R.drawable.premium,R.drawable.deal,R.drawable.banner,R.drawable.premium))
            viewPagerPremium.adapter = premiumStoreBannerViewPagerAdapter
            dotsIndicatorPremium.attachTo(binding?.viewPagerPremium!!)

            bestSellingAdapter = BestSellingAdapter(listOf("","","","",""))
            rvBestSelling.adapter = bestSellingAdapter

            topRatedAdapter = TopRatedAdapter(listOf("","","","",""))
            rvTopRated.adapter = topRatedAdapter

            suggestedStoreAdapter = SuggestedStoreAdapter(listOf("","","","","","","","","",""))
            rvSuggestedStore.adapter = suggestedStoreAdapter

            newestProductAdapter = NewestProductAdapter(listOf("","","","","","","","","",""))
            rvNewestProducts.adapter = newestProductAdapter

            categoryList.clear()
            categoryAdapter = CategoryAdapter(requireActivity(),categoryList,object :CategoryAdapter.CategoryClick{
                override fun onCategoryClick(position: Int) {

                }

            })
            rvCategory.adapter = categoryAdapter

          //  getDashboardApi()
            getSubCategoryApi("1")
        }
    }

    override fun initControl() {

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.dashBoardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                }
                isObserver = false
            }
        }

        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                }
                isObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    categoryList.clear()
                    categoryList.addAll(response.data)
                    categoryAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }

    }

    private fun getDashboardApi(){
        viewModel.getDashBoard()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getCategoryApi(){
        viewModel.getCategory()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSubCategoryApi(id:String){
        viewModel.getSubCategory(id)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


}
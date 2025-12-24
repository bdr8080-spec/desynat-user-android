package com.desynatuser.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.BestSellingAdapter
import com.desynatuser.adapter.CategoryAdapter
import com.desynatuser.adapter.DiscountedDealBannerViewPagerAdapter
import com.desynatuser.adapter.HomeWomenBannerViewPagerAdapter
import com.desynatuser.adapter.NewestProductAdapter
import com.desynatuser.adapter.PremiumStoreBannerViewPagerAdapter
import com.desynatuser.adapter.SuggestedStoreAdapter
import com.desynatuser.adapter.TopRatedAdapter
import com.desynatuser.adapter.ViewPagerAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.data.view_model.LocationViewModel
import com.desynatuser.databinding.FragmentHomeBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    var isObserver = false
    var isCartObserver = false
    private var adapter: ViewPagerAdapter?=null
    private var user: LoginResponse.Data?=null

    var isCategoryObserver = false
    private var bestSellingAdapter: BestSellingAdapter?=null

    private var mainCategoryList = ArrayList<GetCategoryResponse.Data>()
    private var categoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var categoryAdapter: CategoryAdapter?=null
    private var newestProductAdapter: NewestProductAdapter?=null
    private var suggestedStoreAdapter: SuggestedStoreAdapter?=null
    private var topRatedAdapter: TopRatedAdapter?=null
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()

    private var homeWomenBannerViewPagerAdapter : HomeWomenBannerViewPagerAdapter? =null
    private var discountedDealBannerViewPagerAdapter : DiscountedDealBannerViewPagerAdapter? =null
    private var premiumStoreBannerViewPagerAdapter : PremiumStoreBannerViewPagerAdapter? =null
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
        user = SharedPreference.get(requireActivity()).getUser()
        binding?.apply {
            if (!Constants.isGuest){
                tvDeliverTo.text = "Deliver to ${user?.fullName}"
            }
            if (Constants.location!=""){
                tvLocation.text = Constants.location
            }
          /*  adapter = ViewPagerAdapter(this@HomeFragment)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {tab.text = "Men"
                    Constants.Category = "Men"}
                    1 -> {tab.text = "Women"
                        Constants.Category = "Women"}
                    2 ->{ tab.text = "Kids"
                        Constants.Category = "Kids"}
                }
            }.attach()

            viewPager.post {
                viewPager.currentItem = 1
            }*/

            homeWomenBannerViewPagerAdapter = HomeWomenBannerViewPagerAdapter(listOf(
                    R.drawable.banner,
                    R.drawable.premium,
                    R.drawable.deal,
                    R.drawable.banner))
                viewPager.adapter = homeWomenBannerViewPagerAdapter
                dotsIndicator.attachTo(binding?.viewPager!!)

                discountedDealBannerViewPagerAdapter = DiscountedDealBannerViewPagerAdapter(listOf(
                    R.drawable.deal,
                    R.drawable.banner,
                    R.drawable.premium,
                    R.drawable.deal))
                viewPagerDiscountedDeals.adapter = discountedDealBannerViewPagerAdapter
                dotsIndicatorDiscountedDeals.attachTo(binding?.viewPagerDiscountedDeals!!)

                premiumStoreBannerViewPagerAdapter = PremiumStoreBannerViewPagerAdapter(listOf(
                    R.drawable.premium,
                    R.drawable.deal,
                    R.drawable.banner,
                    R.drawable.premium))
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
                        val bundle = Bundle().apply {
                            putString("subCategoryData", Gson().toJson(categoryList[position]))
                        }
                        findNavController().navigate(R.id.action_homeFragment_to_storeFragment,bundle)
                    }

                })
                rvCategory.adapter = categoryAdapter

                //  getDashboardApi()
                getCategoryApi()

            when(Constants.Category){
                "Men"-> {
                    viewMen.isVisible = true
                    viewWomen.isInvisible = true
                    viewKids.isInvisible = true
                }

                "Women"-> {
                viewWomen.isVisible = true
                viewMen.isInvisible = true
                viewKids.isInvisible = true
            }
                    "Kids"-> {
                        viewKids.isVisible = true
                viewMen.isInvisible = true
                        viewWomen.isInvisible = true
            }
            }
            }

        viewCartDetail()

    }

    override fun initControl() {
        binding?.apply {
            tvLocation.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)
            }

            etSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchProductFragment)
            }

            ivMic.setOnClickListener{
                openSpeechDialog()
            }

            ivWishlist.setOnClickListener{
                findNavController().navigate(R.id.action_homeFragment_to_wishlistFragment)
            }

            tvWomen.setOnClickListener {
                Constants.Category = "Women"
                viewWomen.isVisible = true
                viewMen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
            }

            tvMen.setOnClickListener {
                Constants.Category = "Men"
                viewMen.isVisible = true
                viewWomen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
            }

            tvKids.setOnClickListener {
                Constants.Category = "Kids"
                viewKids.isVisible = true
                viewMen.isInvisible = true
                viewWomen.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
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

        locationViewModel.location.observe(viewLifecycleOwner) { (lat, lng) ->
            binding?.tvLocation?.text = Constants.location

        }

        viewModel.dashBoardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                }
                isObserver = false
            }
        }

        viewModel.cartDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                Constants.cartItem = myBagList.size
                (requireActivity() as HomeActivity).updateAdapterCount()
                isCartObserver = false
            }
        }

        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isCategoryObserver) {
                response?.let {
                    mainCategoryList.clear()
                    mainCategoryList.addAll(response.data)
                    SharedPreference.get(requireActivity()).saveCategoryLIst(response.data)
                    getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                }
                isCategoryObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    categoryList.clear()
                    categoryList.addAll(response.data)
                    categoryAdapter?.notifyDataSetChanged()
                    if (categoryList.size==0){
                        binding?.tvNoCategory?.isVisible = true
                    }
                    else{
                        binding?.tvNoCategory?.isVisible = false
                    }
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
        isCategoryObserver = true
    }

    private fun getSubCategoryApi(id:String){
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        viewModel.getSubCategory(id)
        isObserver = true
    }


    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city") ?: "N/A"
            val street = data?.getStringExtra("street") ?: "N/A"
            val state = data?.getStringExtra("state") ?: "N/A"
            val pincode = data?.getStringExtra("pincode") ?: "N/A"
            val houseno = data?.getStringExtra("houseno") ?: "N/A"
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            val countryField = data?.getStringExtra("country") ?: "N/A"

            val lat = data?.getDoubleExtra("lat", 0.0)?:0.0
            val lng = data?.getDoubleExtra("lng", 0.0)?:0.0
            Constants.latitude = lat
            Constants.longitude = lng
            Constants.location = addressH
            binding?.tvLocation?.text = addressH
        }
    }

    private fun viewCartDetail(){
        viewModel.viewCartDetail()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCartObserver = true
    }

    private fun openSpeechDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            showToast("Speech recognition not supported")
        }
    }

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val list = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = list?.firstOrNull()

                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
                    binding?.etSearch?.setSelection(text.length)
                }
            }
        }

}
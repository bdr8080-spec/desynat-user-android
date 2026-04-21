package com.desynatuser.ui.home.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
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
import com.desynatuser.data.view_model.LocationViewModel
import com.desynatuser.databinding.FragmentHomeBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.ui.home.fragment.response.GetAllSuggestStoreResponse
import com.desynatuser.ui.home.fragment.response.GetBestSellingProductResponse
import com.desynatuser.ui.home.fragment.response.GetDealBannerResponse
import com.desynatuser.ui.home.fragment.response.GetNewestProductResponse
import com.desynatuser.ui.home.fragment.response.GetTopRatedProductResponse
import com.desynatuser.ui.home.imageDetector.AnnotateRequest
import com.desynatuser.ui.home.imageDetector.Feature
import com.desynatuser.ui.home.imageDetector.ImageContent
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.vision.VisionRetrofit
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    var isObserver = false
    var isAdminObserver = false
    var isHomeBannerObserver = false
    var isHomeAllBannerObserver = false
    var isHomeBottomBannerObserver = false
    var isHomeDealBannerObserver = false
    var isHomeCategoryBannerObserver = false
    var isCartObserver = false
    var isNotificationObserver = false
    private var isSuggestedStoreObserver = false
    private var isBestSellingObserver = false
    private var isTopRatedObserver = false
    private var isNewestObserver = false

    private val topRatedList = ArrayList<GetTopRatedProductResponse.Data>()
    private val bestSellingList = ArrayList<GetBestSellingProductResponse.Data>()
    private val newestProductList = ArrayList<GetNewestProductResponse.Data>()
    private val suggestedStoreList = ArrayList<GetAllSuggestStoreResponse.Data>()


    private var hasNavigatedFromML = false

    private var user: LoginResponse.Data? = null

    var isCategoryObserver = false
    private var bestSellingAdapter: BestSellingAdapter? = null
    private val myStories = ArrayList<GetSellerStoryResponse.Data>()

    private var mainCategoryList = ArrayList<GetCategoryResponse.Data>()
    private var categoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var categoryAdapter: CategoryAdapter? = null
    private var newestProductAdapter: NewestProductAdapter? = null
    private var suggestedStoreAdapter: SuggestedStoreAdapter? = null
    private var topRatedAdapter: TopRatedAdapter? = null
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var homeBannerList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var premiumList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var categoryBannerList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var discountList = ArrayList<GetDealBannerResponse.Data>()
    private var homeWomenBannerViewPagerAdapter: HomeWomenBannerViewPagerAdapter? = null
    private var discountedDealBannerViewPagerAdapter: DiscountedDealBannerViewPagerAdapter? = null
    private var premiumStoreBannerViewPagerAdapter: PremiumStoreBannerViewPagerAdapter? = null

    private lateinit var imagePicker: ImagePicker
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    var selectedPos = -1
    var selectedType = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onMessageEvent(null)
        }
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        Constants.language = SharedPreference.get(requireActivity()).language
        getAdminStory()
        user = SharedPreference.get(requireActivity()).getUser()
        binding?.apply {
            if (SharedPreference.get(requireActivity()).accessToken != "") {
                tvDeliverTo.text = "Deliver to ${user?.fullName}"
                notificationCount()
            }
            if (Constants.location != "") {
                tvLocation.text = Constants.location
            }

            homeWomenBannerViewPagerAdapter =
                HomeWomenBannerViewPagerAdapter(requireActivity(), homeBannerList, object :
                    HomeWomenBannerViewPagerAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        Log.e(
                            "storeid",
                            "onItemClick: ${homeBannerList[position]?.sellerDetails?.storeInfo?.id?.toString()}"
                        )
                        val bundle = Bundle().apply {
                            putString(
                                "storeId",
                                homeBannerList[position]?.sellerDetails?.storeInfo?.id?.toString()
                            )
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_storeDetailFragment,
                            bundle
                        )
                    }

                })
            viewPager.adapter = homeWomenBannerViewPagerAdapter
            TabLayoutMediator(dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

            discountedDealBannerViewPagerAdapter =
                DiscountedDealBannerViewPagerAdapter(requireActivity(), discountList, object :
                    DiscountedDealBannerViewPagerAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString(
                                "storeId",
                                discountList[position]?.sellerDealrecord?.get(0)?.productDetails?.storeId.toString()
                            )
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_storeDetailFragment,
                            bundle
                        )
                    }

                })
            viewPagerDiscountedDeals.adapter = discountedDealBannerViewPagerAdapter
            TabLayoutMediator(
                dotsIndicatorDiscountedDeals,
                binding!!.viewPagerDiscountedDeals
            ) { _, _ -> }.attach()

            premiumStoreBannerViewPagerAdapter =
                PremiumStoreBannerViewPagerAdapter(requireActivity(), premiumList, object :
                    PremiumStoreBannerViewPagerAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString(
                                "storeId",
                                premiumList[position]?.sellerDetails?.storeInfo?.id?.toString()
                            )
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_storeDetailFragment,
                            bundle
                        )
                    }

                })
            viewPagerPremium.adapter = premiumStoreBannerViewPagerAdapter
            TabLayoutMediator(dotsIndicatorPremium, binding!!.viewPagerPremium) { _, _ -> }.attach()

            bestSellingAdapter = BestSellingAdapter(requireActivity(), bestSellingList, object :
                BestSellingAdapter.OnClick {
                override fun onItemClick(position: Int, type: Int) {
                    when (type) {
                        1 -> {
                            selectedPos = position
                            selectedType = 1
                            addToWishList()
                        }

                        2 -> {
                            val data = bestSellingList[position]
                            CommonMethod.shareProductWithImage(
                                requireActivity(),
                                "Product name: ${data?.productNameEn}",
                                "Description: ${data?.descriptionEn}",
                                data?.productVariants?.get(0)?.variantImage?.get(0) ?: "",
                                "https://desynat.com/user/product/detail-page/${data.id}"
                            )
                        }

                        3 -> {
                            val bundle = Bundle().apply {
                                putString("productId", bestSellingList[position].id.toString())
                            }
                            findNavController().navigate(
                                R.id.action_homeFragment_to_productDetailFragment,
                                bundle
                            )
                        }
                    }
                }

            })
            rvBestSelling.adapter = bestSellingAdapter

            topRatedAdapter = TopRatedAdapter(requireActivity(), topRatedList, object :
                TopRatedAdapter.OnClick {
                override fun onItemClick(position: Int, type: Int) {
                    when (type) {
                        1 -> {
                            selectedPos = position
                            selectedType = 2
                            addToWishList()
                        }

                        2 -> {
                            val data = topRatedList[position]
                            CommonMethod.shareProductWithImage(
                                requireActivity(),
                                "Product name: ${data?.productNameEn}",
                                "Description: ${data?.descriptionEn}",
                                data?.productVariants?.get(0)?.variantImage?.get(0) ?: "",
                                "https://desynat.com/user/product/detail-page/${data.id}"
                            )
                        }

                        3 -> {
                            val bundle = Bundle().apply {
                                putString("productId", topRatedList[position].id.toString())
                            }
                            findNavController().navigate(
                                R.id.action_homeFragment_to_productDetailFragment,
                                bundle
                            )
                        }
                    }


                }

            })
            rvTopRated.adapter = topRatedAdapter

            suggestedStoreAdapter =
                SuggestedStoreAdapter(requireActivity(), suggestedStoreList, object :
                    SuggestedStoreAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString("storeId", suggestedStoreList[position]?.id.toString())
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_storeDetailFragment,
                            bundle
                        )
                    }

                })
            rvSuggestedStore.adapter = suggestedStoreAdapter

            newestProductAdapter =
                NewestProductAdapter(requireActivity(), newestProductList, object :
                    NewestProductAdapter.OnClick {
                    override fun onItemClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                selectedPos = position
                                selectedType = 3
                                addToWishList()
                            }

                            2 -> {
                                val data = newestProductList[position]
                                CommonMethod.shareProductWithImage(
                                    requireActivity(),
                                    "Product name: ${data?.productNameEn}",
                                    "Description: ${data?.descriptionEn}",
                                    data?.productVariants?.get(0)?.variantImage?.get(0) ?: "",
                                    "https://desynat.com/user/product/detail-page/${data.id}"
                                )
                            }

                            3 -> {
                                val bundle = Bundle().apply {
                                    putString(
                                        "productId",
                                        newestProductList[position].id.toString()
                                    )
                                }
                                findNavController().navigate(
                                    R.id.action_homeFragment_to_productDetailFragment,
                                    bundle
                                )
                            }
                        }
                    }
                })
            rvNewestProducts.adapter = newestProductAdapter

            categoryList.clear()
            categoryAdapter = CategoryAdapter(
                requireActivity(),
                categoryList,
                object : CategoryAdapter.CategoryClick {
                    override fun onCategoryClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString("subCategoryData", Gson().toJson(categoryList[position]))
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_storeFragment,
                            bundle
                        )
                    }

                })
            rvCategory.adapter = categoryAdapter

            getCategoryApi()

            when (Constants.Category) {
                "Men" -> {
                    viewMen.isVisible = true
                    viewWomen.isInvisible = true
                    viewKids.isInvisible = true
                }

                "Women" -> {
                    viewWomen.isVisible = true
                    viewMen.isInvisible = true
                    viewKids.isInvisible = true
                }

                "Kids" -> {
                    viewKids.isVisible = true
                    viewMen.isInvisible = true
                    viewWomen.isInvisible = true
                }
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    val bitmap = uriToBitmap(requireContext(), uri)
                    callVisionApi(bitmap)
                }
            }

        }

        if (SharedPreference.get(requireActivity()).accessToken != "") {
            viewCartDetail()
        }

        getHomeScreenAllBanner()
        getHomeScreenBottomBanner()
        getHomeScreenDealBanner()
        //  getHomeScreenCategoryBanner()

        getSuggestedStore()
        getNewestProduct()
        getBestSellingProduct()
        getTopRatedProduct()

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

            ivSupportBot.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_TidioChatFragment)
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }

            ivCamera.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            ivChat.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken != "") {
                    findNavController().navigate(R.id.action_homeFragment_to_chatListFragment)
                } else {
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivWishlist.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken != "") {
                    findNavController().navigate(R.id.action_homeFragment_to_wishlistFragment)
                } else {
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivNotification.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }

            tvWomen.setOnClickListener {
                Constants.Category = "Women"
                viewWomen.isVisible = true
                viewMen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenDealBanner()
            }

            tvMen.setOnClickListener {
                Constants.Category = "Men"
                viewMen.isVisible = true
                viewWomen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenDealBanner()
            }

            tvKids.setOnClickListener {
                Constants.Category = "Kids"
                viewKids.isVisible = true
                viewMen.isInvisible = true
                viewWomen.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenDealBanner()
            }

            ivAdminStory.setOnClickListener {
                if (myStories.size != 0) {
                    val bundle = Bundle().apply {
                        putString("from", "admin")
                        putString("storyData", Gson().toJson(myStories))
                    }
                    findNavController().navigate(
                        R.id.action_homeFragment_to_viewStoryFragment,
                        bundle
                    )
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver || isNotificationObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                binding?.clMain?.isVisible = true
                binding?.progressBar?.isVisible = false
                isObserver = false
                isNotificationObserver = false
            }
        }

        viewModel.userNotificationCountResult.observe(viewLifecycleOwner) { response ->
            if (isNotificationObserver) {

                response?.let {
                    if (response?.data != null && response?.data != 0) {
                        setNotificationCount(response.data)
                    } else {
                        SharedPreference.get(requireActivity()).badgeCount = 0
                        ShortcutBadger.removeCount(requireActivity())

                    }
                }
                isNotificationObserver = false
            }
        }

        locationViewModel.location.observe(viewLifecycleOwner) { (lat, lng) ->
            binding?.tvLocation?.text = Constants.location
        }

        viewModel.dashBoardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                response?.let {

                }
                isObserver = false
            }
        }

        viewModel.getHomeScreenAllBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeAllBannerObserver) {
                response?.let {
                    val list = response.data
                    homeBannerList.clear()
                    homeBannerList.addAll(response.data)
                    homeWomenBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkBannerData()

                }
                isHomeAllBannerObserver = false
            }
        }

        viewModel.getDealBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeDealBannerObserver) {
                response?.let {
                    val list = response.data
                    discountList.clear()
                    discountList.addAll(response.data)
                    discountedDealBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkDiscountBannerData()
                }
                isHomeDealBannerObserver = false
            }
        }

        viewModel.getHomeScreenBottomBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeBottomBannerObserver) {
                response?.let {

                    premiumList.clear()
                    premiumList.addAll(response.data)
                    premiumStoreBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkPremiumBannerData()
                }
                isHomeBottomBannerObserver = false
            }
        }

        viewModel.getHomeScreenCategoryBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeCategoryBannerObserver) {
                response?.let {
                    categoryBannerList.clear()
                    categoryBannerList.addAll(response.data)

                }
                isHomeCategoryBannerObserver = false
            }
        }

        viewModel.cartHomeDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
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


        viewModel.getAdminStoryResult.observe(viewLifecycleOwner) { response ->
            if (isAdminObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    setAdminStory()

                }
                isAdminObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                response?.let {
                    categoryList.clear()
                    categoryList.addAll(response.data)
                    categoryAdapter?.notifyDataSetChanged()
                    if (categoryList.size == 0) {
                        binding?.tvNoCategory?.isVisible = true
                    } else {
                        binding?.tvNoCategory?.isVisible = false
                    }
                }
                isObserver = false
            }
        }

        viewModel.getSuggestedStoreResult.observe(viewLifecycleOwner) { response ->
            if (isSuggestedStoreObserver) {
                response?.let {
                    suggestedStoreList.clear()
                    suggestedStoreList.addAll(response.data)
                    suggestedStoreAdapter?.notifyDataSetChanged()
                    if (suggestedStoreList.size == 0) {
                        binding?.tvNoSuggested?.isVisible = true
                    } else {
                        binding?.tvNoSuggested?.isVisible = false
                    }

                }
                isSuggestedStoreObserver = false
            }
        }

        viewModel.getNewestProductResult.observe(viewLifecycleOwner) { response ->
            if (isNewestObserver) {
                response?.let {
                    newestProductList.clear()
                    newestProductList.addAll(response.data)
                    newestProductAdapter?.notifyDataSetChanged()
                    if (newestProductList.size == 0) {
                        binding?.tvNoNewest?.isVisible = true
                    } else {
                        binding?.tvNoNewest?.isVisible = false
                    }
                }
                isNewestObserver = false
            }
        }

        viewModel.getTopRatedProductResult.observe(viewLifecycleOwner) { response ->
            if (isTopRatedObserver) {
                response?.let {
                    topRatedList.clear()
                    topRatedList.addAll(response.data)
                    topRatedAdapter?.notifyDataSetChanged()
                    if (topRatedList.size == 0) {
                        binding?.tvNoTopRated?.isVisible = true
                    } else {
                        binding?.tvNoTopRated?.isVisible = false
                    }
                }
                isTopRatedObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                when (selectedType) {
                    1 -> {
                        if (bestSellingList[selectedPos].wishlist == 1) {
                            showToast("Product removed from your wishlist successfully")
                            bestSellingList[selectedPos].wishlist = 0
                        } else {
                            showToast("Product added to your wishlist successfully")
                            bestSellingList[selectedPos].wishlist = 1
                        }
                        bestSellingAdapter?.notifyItemChanged(selectedPos)

                    }

                    2 -> {
                        if (topRatedList[selectedPos].wishlist == 1) {
                            showToast("Product removed from your wishlist successfully")
                            topRatedList[selectedPos].wishlist = 0
                        } else {
                            showToast("Product added to your wishlist successfully")
                            topRatedList[selectedPos].wishlist = 1
                        }
                        topRatedAdapter?.notifyItemChanged(selectedPos)

                    }

                    3 -> {
                        if (newestProductList[selectedPos].wishlist == 1) {
                            showToast("Product removed from your wishlist successfully")
                            newestProductList[selectedPos].wishlist = 0
                        } else {
                            showToast("Product added to your wishlist successfully")
                            newestProductList[selectedPos].wishlist = 1
                        }
                        newestProductAdapter?.notifyItemChanged(selectedPos)

                    }
                }

                selectedPos = -1
                selectedType = 0
                isObserver = false
            }

        }


        viewModel.getBestSellingProductResult.observe(viewLifecycleOwner) { response ->
            if (isBestSellingObserver) {
                response?.let {
                    bestSellingList.clear()
                    bestSellingList.addAll(response.data)
                    bestSellingAdapter?.notifyDataSetChanged()
                    if (bestSellingList.size == 0) {
                        binding?.tvNoBestSelling?.isVisible = true
                    } else {
                        binding?.tvNoBestSelling?.isVisible = false
                    }
                }
                isBestSellingObserver = false
            }
        }


    }

    private fun setAdminStory() {
        /*  binding?.apply {
          //    ivAdminStory.segmentCount = myStories.size
              if (myStories.size!=0){
                  ivAdminStory.setImageResource(R.drawable.ic_admin_highlight_story)
              }
              else{
                  ivAdminStory.setImageResource(R.drawable.ic_admin_story)
              }
          }*/

        binding?.ivHighlightStory?.isVisible = myStories.isNotEmpty()

    }

    private fun getAdminStory() {
        val map = HashMap<String, Any>()
        viewModel.getAdminStory(map)
        isAdminObserver = true
    }

    private fun setNotificationCount(data: Int) {
        binding?.relCount?.isVisible = true
        binding?.tvCount?.text = data.toString()
        ShortcutBadger.applyCount(requireActivity(), data)
    }

    private fun getDashboardApi() {
        viewModel.getDashBoard()
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun getCategoryApi() {
        viewModel.getCategory()
        isCategoryObserver = true
    }

    private fun getSubCategoryApi(id: String) {
        binding?.progressBar?.isVisible = true
        viewModel.getSubCategory(id)
        isObserver = true
    }

    private fun getHomeScreenAllBanner() {
        val map = HashMap<String, Any>()
        map["type"] = "1"
        viewModel.getHomeScreenAllBanner(map)
        isHomeAllBannerObserver = true
    }

    private fun getHomeScreenBottomBanner() {
        val map = HashMap<String, Any>()
        map["type"] = "2"
        viewModel.getHomeScreenBottomBanner(map)
        isHomeBottomBannerObserver = true
    }

    private fun getHomeScreenDealBanner() {
        val map = HashMap<String, Any>()
        map["category"] = when (Constants.Category) {
            "Men" -> "1"
            "Women" -> "2"
            "Kids" -> "3"
            else -> "2"
        }
        viewModel.getDealBanner(map)
        isHomeDealBannerObserver = true
    }

    private fun getHomeScreenCategoryBanner() {
        val map = HashMap<String, Any>()
        map["type"] = "3"
        map["category"] = when (Constants.Category) {
            "Men" -> "1"
            "Women" -> "2"
            "Kids" -> "3"
            else -> "2"
        }
        viewModel.getHomeScreenCategoryBanner(map)
        isHomeCategoryBannerObserver = true
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

            val lat = data?.getDoubleExtra("lat", 0.0) ?: 0.0
            val lng = data?.getDoubleExtra("lng", 0.0) ?: 0.0
            Constants.latitude = lat
            Constants.longitude = lng
            Constants.location = addressH
            binding?.tvLocation?.text = addressH
        }
    }

    private fun viewCartDetail() {
        viewModel.viewHomeCartDetail()
        isCartObserver = true
    }

    private fun notificationCount() {
        viewModel.userNotificationCount()
        isNotificationObserver = true
    }

    private fun getSuggestedStore() {
        viewModel.getSuggestedStore()
        isSuggestedStoreObserver = true
    }

    private fun getTopRatedProduct() {
        viewModel.getTopRatedProduct()
        isTopRatedObserver = true
    }

    private fun getNewestProduct() {
        viewModel.getNewestProduct()
        isNewestObserver = true
    }

    private fun getBestSellingProduct() {
        viewModel.getBestSellingProduct()
        isBestSellingObserver = true
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
                    if (!text.isNullOrEmpty()) {
                        val bundle = Bundle().apply {
                            putString("search", text)
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_searchProductListFragment,
                            bundle
                        )
                    }
                }
            }
        }

    fun callVisionApi(bitmap: Bitmap) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        val base64Image = bitmapToBase64(bitmap)

        val request = VisionRequestBody(
            requests = listOf(
                AnnotateRequest(
                    image = ImageContent(base64Image),
                    features = listOf(
                        Feature(type = "LABEL_DETECTION", maxResults = 5),
                        Feature(type = "OBJECT_LOCALIZATION", maxResults = 5),
                        Feature(type = "LOGO_DETECTION", maxResults = 5),
                        Feature(type = "WEB_DETECTION", maxResults = 5),
                        Feature(type = "IMAGE_PROPERTIES", maxResults = 5),
                        Feature(type = "TEXT_DETECTION", maxResults = 5),
                        Feature(type = "BARCODE_DETECTION", maxResults = 5)
                    )
                )
            )
        )

        lifecycleScope.launch {
            try {
                val response = VisionRetrofit.api.detectImage(
                    apiKey = "AIzaSyBk1iHVph8nvXAn0uigTvPzKDpb9cslR18",
                    request = request
                )
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_RESPONSE", response.body().toString() ?: "Error")

                val labels = response.body()?.responses
                    ?.firstOrNull()
                    ?.labelAnnotations
                    ?.map { it.description }
                    ?: emptyList()

                searchThroughImage(labels)

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_ERROR", e.message ?: "Error")
            }
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun checkBannerData() {
        binding?.apply {
            if (homeBannerList.size != 0) {
                viewPager.isVisible = true
                dotsIndicator.isVisible = true
                autoScrollViewPager(viewPager, bannerHandler)
            } else {
                homeBannerList.add(GetHomeScreenAllBannerResponse.Data())
                homeWomenBannerViewPagerAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun checkDiscountBannerData() {
        binding?.apply {
            if (discountList.size != 0) {
                viewPagerDiscountedDeals.isVisible = true
                dotsIndicatorDiscountedDeals.isVisible = true
                tvDealDiscounted.isVisible = true
                autoScrollViewPager(viewPagerDiscountedDeals, discountHandler)
            } else {
                viewPagerDiscountedDeals.isVisible = false
                dotsIndicatorDiscountedDeals.isVisible = false
                tvDealDiscounted.isVisible = false
            }
        }
    }

    private fun checkPremiumBannerData() {
        binding?.apply {
            if (premiumList.size != 0) {
                tvBrowseSomePremium.isVisible = true
                viewPagerPremium.isVisible = true
                dotsIndicatorPremium.isVisible = true
                autoScrollViewPager(viewPagerPremium, premiumHandler)
            } else {
                tvBrowseSomePremium.isVisible = false
                viewPagerPremium.isVisible = false
                dotsIndicatorPremium.isVisible = false
            }
        }
    }

    private fun searchThroughImage(detectedLabel: List<String>) {
        val navController = findNavController()
        if (
            !hasNavigatedFromML &&
            navController.currentDestination?.id == R.id.home
        ) {
            hasNavigatedFromML = true

            val bundle = Bundle().apply {
                putStringArrayList("searchList", ArrayList(detectedLabel))
            }

            navController.navigate(
                R.id.action_homeFragment_to_searchProductListFragment,
                bundle
            )
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private val bannerHandler = Handler(Looper.getMainLooper())
    private val discountHandler = Handler(Looper.getMainLooper())
    private val premiumHandler = Handler(Looper.getMainLooper())

    private fun autoScrollViewPager(
        viewPager: ViewPager2,
        handler: Handler,
        delay: Long = 4000L
    ) {
        val runnable = object : Runnable {
            override fun run() {
                val adapter = viewPager.adapter ?: return
                if (adapter.itemCount <= 1) return

                val nextItem =
                    if (viewPager.currentItem == adapter.itemCount - 1) 0
                    else viewPager.currentItem + 1

                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delay)
            }
        }

        handler.postDelayed(runnable, delay)

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, delay)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacksAndMessages(null)
        discountHandler.removeCallbacksAndMessages(null)
        premiumHandler.removeCallbacksAndMessages(null)
    }

    override fun onStart() {
        super.onStart()
        Constants.currentScreen = "home"
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Constants.currentScreen = ""
        EventBus.getDefault().unregister(this)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String? = null) {
        if (event != null) {
            notificationCount()
        }
    }

    private fun addToWishList() {
        val map = HashMap<String, Any>()
        when (selectedType) {
            1 -> {
                map["product_id"] = bestSellingList[selectedPos].id
            }

            2 -> {
                map["product_id"] = topRatedList[selectedPos].id
            }

            3 -> {
                map["product_id"] = newestProductList[selectedPos].id
            }
        }

        viewModel.addToWishlist(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

}

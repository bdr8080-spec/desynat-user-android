package com.desynatuser.ui.home.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
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
import com.desynatuser.adapter.HomeFullScreenBannerAdapter
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
import com.desynatuser.databinding.HomeBannerDialogBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.ui.home.imageDetector.AnnotateRequest
import com.desynatuser.ui.home.imageDetector.Feature
import com.desynatuser.ui.home.imageDetector.ImageContent
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.ui.response.GetHomeScreenPopUpBanner
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
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    var isObserver = false
    var isHomeBannerObserver = false
    var isHomeAllBannerObserver = false
    var isHomeBottomBannerObserver = false
    var isHomeCategoryBannerObserver = false
    var isCartObserver = false
    private var hasNavigatedFromML = false

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
    private var homeBannerList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var premiumList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var discountList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var homeWomenBannerViewPagerAdapter : HomeWomenBannerViewPagerAdapter? =null
    private var discountedDealBannerViewPagerAdapter : DiscountedDealBannerViewPagerAdapter? =null
    private var premiumStoreBannerViewPagerAdapter : PremiumStoreBannerViewPagerAdapter? =null

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
            if (SharedPreference.get(requireActivity()).accessToken!=""){
                tvDeliverTo.text = "Deliver to ${user?.fullName}"
            }
            if (Constants.location!=""){
                tvLocation.text = Constants.location
            }

            homeWomenBannerViewPagerAdapter = HomeWomenBannerViewPagerAdapter(requireActivity(),homeBannerList)
                viewPager.adapter = homeWomenBannerViewPagerAdapter
            TabLayoutMediator(dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

                discountedDealBannerViewPagerAdapter = DiscountedDealBannerViewPagerAdapter(requireActivity(),discountList)
                viewPagerDiscountedDeals.adapter = discountedDealBannerViewPagerAdapter
            TabLayoutMediator(dotsIndicatorDiscountedDeals, binding!!.viewPagerDiscountedDeals) { _, _ -> }.attach()

                premiumStoreBannerViewPagerAdapter = PremiumStoreBannerViewPagerAdapter(requireActivity(),premiumList)
                viewPagerPremium.adapter = premiumStoreBannerViewPagerAdapter
            TabLayoutMediator(dotsIndicatorPremium, binding!!.viewPagerPremium) { _, _ -> }.attach()

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

        if (SharedPreference.get(requireActivity()).accessToken!=""==false){
            viewCartDetail()
        }

        getHomeScreenAllBanner()
        getHomeScreenBottomBanner()
        getHomeScreenCategoryBanner()

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

            ivCamera.setOnClickListener{
                imagePicker.showImageSourceDialog()
            }

            ivChat.setOnClickListener{
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    findNavController().navigate(R.id.action_homeFragment_to_chatListFragment)
                } else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivWishlist.setOnClickListener{
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    findNavController().navigate(R.id.action_homeFragment_to_wishlistFragment)
                } else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            tvWomen.setOnClickListener {
                Constants.Category = "Women"
                viewWomen.isVisible = true
                viewMen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenCategoryBanner()
            }

            tvMen.setOnClickListener {
                Constants.Category = "Men"
                viewMen.isVisible = true
                viewWomen.isInvisible = true
                viewKids.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenCategoryBanner()
            }

            tvKids.setOnClickListener {
                Constants.Category = "Kids"
                viewKids.isVisible = true
                viewMen.isInvisible = true
                viewWomen.isInvisible = true
                getSubCategoryApi(mainCategoryList.find { it.name == Constants.Category }?.id.toString())
                getHomeScreenCategoryBanner()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                binding?.clMain?.isVisible = true
                binding?.progressBar?.isVisible = false
                isObserver = false
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

                    homeBannerList.clear()
                    homeBannerList.addAll(response.data)
                    homeWomenBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkBannerData()

                }
                isHomeAllBannerObserver = false
            }
        }

        viewModel.getHomeScreenBottomBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeBottomBannerObserver) {
                response?.let {
                    discountList.clear()
                    discountList.addAll(response.data)
                    discountedDealBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkDiscountBannerData()
                }
                isHomeBottomBannerObserver = false
            }
        }

        viewModel.getHomeScreenBottomBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeBottomBannerObserver) {
                response?.let {
                    discountList.clear()
                    discountList.addAll(response.data)
                    discountedDealBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkDiscountBannerData()
                }
                isHomeBottomBannerObserver = false
            }
        }

        viewModel.getHomeScreenCategoryBannerResult.observe(viewLifecycleOwner) { response ->
            if (isHomeCategoryBannerObserver) {
                response?.let {
                    premiumList.clear()
                    premiumList.addAll(response.data)
                    premiumStoreBannerViewPagerAdapter?.notifyDataSetChanged()
                    checkPremiumBannerData()
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

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
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
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun getCategoryApi(){
        viewModel.getCategory()
        isCategoryObserver = true
    }

    private fun getSubCategoryApi(id:String){
        binding?.progressBar?.isVisible = true
        viewModel.getSubCategory(id)
        isObserver = true
    }

    private fun getHomeScreenAllBanner(){
        val map = HashMap<String,Any>()
        map["type"] = "1"
        viewModel.getHomeScreenAllBanner(map)
        isHomeAllBannerObserver = true
    }

    private fun getHomeScreenBottomBanner(){
        val map = HashMap<String,Any>()
        map["type"] = "2"
        viewModel.getHomeScreenBottomBanner(map)
        isHomeBottomBannerObserver = true
    }

    private fun getHomeScreenCategoryBanner(){
        val map = HashMap<String,Any>()
        map["type"] = "3"
        map["category"] = when(Constants.Category){
            "Men" ->  "1"
            "Women" ->  "2"
            "Kids" ->  "3"
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

            val lat = data?.getDoubleExtra("lat", 0.0)?:0.0
            val lng = data?.getDoubleExtra("lng", 0.0)?:0.0
            Constants.latitude = lat
            Constants.longitude = lng
            Constants.location = addressH
            binding?.tvLocation?.text = addressH
        }
    }

    private fun viewCartDetail(){
        viewModel.viewHomeCartDetail()
        binding?.progressBar?.isVisible = true
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

    private fun searchThroughImage(detectedLabel:List<String>){
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

}

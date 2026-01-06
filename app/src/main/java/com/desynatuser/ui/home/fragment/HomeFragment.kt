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
import com.desynatuser.adapter.HomeWomenBannerViewPagerAdapter
import com.desynatuser.adapter.NewestProductAdapter
import com.desynatuser.adapter.PremiumStoreBannerViewPagerAdapter
import com.desynatuser.adapter.ProductImageAdapter
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
    var isDetactImage = false
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

//        if (Constants.isFromLoginSignup){
//            showBannerDialog(arrayListOf("https://desynat-bucket.s3.me-south-1.amazonaws.com/Product/1764228507_6927fd9b789d1.png","https://desynat-bucket.s3.me-south-1.amazonaws.com/Product/1764228507_6927fd9b789d1.png"))
//            Constants.isFromLoginSignup = false
//        }
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

        viewCartDetail()

    }

    private fun showBannerDialog(imageList: ArrayList<String>) {

        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = HomeBannerDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes = attributes.apply {
                gravity = Gravity.CENTER
            }
        }

        dialog.setCancelable(false)

        val handler = Handler(Looper.getMainLooper())
        val dismissRunnable = Runnable {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        dialogBinding.apply {

            tvSkip.setOnClickListener {
                handler.removeCallbacks(dismissRunnable)
                dialog.dismiss()
            }

            val productImageAdapter =
                ProductImageAdapter(requireActivity(), imageList, object :
                    ProductImageAdapter.OnClick {
                    override fun onClick(position: Int) {}
                })

            viewPager.adapter = productImageAdapter

            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

            viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (position == imageList.size - 1) {
                        handler.postDelayed(dismissRunnable, 500)
                    }
                }
            })
        }

        dialog.setOnDismissListener {
            handler.removeCallbacks(dismissRunnable)
        }

        dialog.show()
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

}

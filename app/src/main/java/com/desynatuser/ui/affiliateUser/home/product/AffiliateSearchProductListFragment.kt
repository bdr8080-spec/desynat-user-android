package com.desynatuser.ui.affiliateUser.home.product

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliateProductAdapter
import com.desynatuser.adapter.FilterBrandAdapter
import com.desynatuser.adapter.FilterCategoryAdapter
import com.desynatuser.adapter.FilterColorAdapter
import com.desynatuser.adapter.FilterSizeAdapter
import com.desynatuser.adapter.SortListAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateSearchProductListBinding
import com.desynatuser.databinding.GetProductLinkDialogBinding
import com.desynatuser.databinding.ProductFilterBinding
import com.desynatuser.databinding.SortProductBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.ui.home.imageDetector.AnnotateRequest
import com.desynatuser.ui.home.imageDetector.Feature
import com.desynatuser.ui.home.imageDetector.ImageContent
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.desynatuser.vision.VisionRetrofit
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class AffiliateSearchProductListFragment : BaseFragment() {
    private var binding : FragmentAffiliateSearchProductListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    var isObserver = false
    var isSortObserver = false
    private var productAdapter: AffiliateProductAdapter? = null
    private var productList = ArrayList<GetAffiliateAllProductResponse.Data.Data>()
    private var isFirstLoad = true
    var selectedPos = -1
    private var sortFilter: String?=null
    private var sortList = ArrayList<GetStoreSortListResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null

    private var sortAdapter: SortListAdapter? = null
    private var filterColorAdapter: FilterColorAdapter? = null
    private var filterBrandAdapter: FilterBrandAdapter? = null
    private var filterSizeAdapter: FilterSizeAdapter? = null
    private var filterCategoryAdapter: FilterCategoryAdapter? = null

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var sizeList = ArrayList<GetSizeListResponse.Data>()
    private var colorList = ArrayList<GetAllColorListResponse.Data>()
    private var brandList = ArrayList<GetAllBrandListResponse.Data>()

    private var selectedBrandList = ArrayList<GetAllBrandListResponse.Data>()
    private var selectedSizeList = ArrayList<GetSizeListResponse.Data>()
    private var selectedColorList = ArrayList<GetAllColorListResponse.Data>()
    private var selectedCategory: GetCategoryResponse.Data?=null
    var selectedDiscount: Int? = null
    var selectedRating: Int? = null
    var selectedDeliveryTime: String? = null
    var selectedMinPrice: Int = 0
    var selectedMaxPrice: Int = 0

    var isBrandObserver = false
    var isCategoryObserever = false
    var isSizeObserver = false
    var isColorObserver = false
    val newColorList = ArrayList<GetAllColorListResponse.Data>()

    var searchData = ""
    private var searchList: ArrayList<String> = arrayListOf()

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

    private var linkDialog: Dialog?=null
    private var dialogBinding: GetProductLinkDialogBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateSearchProductListBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        searchData = arguments?.getString("search").toString()
        searchList = arguments?.getStringArrayList("searchList") ?: arrayListOf()
        binding?.apply {
            productAdapter = AffiliateProductAdapter(requireActivity(),productList,object: AffiliateProductAdapter.ProductClick{
                override fun onProductClick(position: Int,type:Int) {
                    when(type){
                        1->{

                        }

                        2->{
                            selectedPos = position
                            getLinkDialog()
                        }
                    }

                }

            })
            rvProduct.adapter = productAdapter
            if (searchData!=null&&searchData!="null"&&searchData!=""){
                lastQuery = searchData
                etSearch.setText(searchData)
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(800)
                        affiliateProductSearch()
                    }

                }
            })

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
        if (isFirstLoad) {
            affiliateProductSearch()
            isFirstLoad = false
        }
    }

    private fun getLinkDialog() {
        linkDialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = GetProductLinkDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding?.root.let { it?.let { it1 -> linkDialog?.setContentView(it1) } }

        val window: Window = linkDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        linkDialog?.setCancelable(true)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding?.apply {
            getAffiliateProductLink()
            tvToolbar.setOnClickListener {
                linkDialog?.dismiss()
            }

            tvCopyLink.setOnClickListener {
                CommonMethod.copyText(requireActivity(),tvLink.text.toString())
            }

            btnAdd.setOnClickListener {
                addAffiliateProduct()
            }
        }
        linkDialog?.show()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvFilter.setOnClickListener {
                productFilterDialog()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }

            ivCamera.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvSort.setOnClickListener {
                sortProductDialog()
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

        viewModel.addAffiliateProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    linkDialog?.dismiss()
                    CommonMethod.showSuccessDialog(requireActivity(),"Product added successfully !")
                    productList.removeAt(selectedPos)
                    productAdapter?.notifyItemRemoved(selectedPos)
                    productAdapter?.notifyItemRangeChanged(selectedPos, productList.size - selectedPos)
                    checkData()
                    selectedPos = -1
                }
                isObserver = false
            }
        }

        viewModel.getAffiliateProductLinkResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    dialogBinding?.tvLink?.text = response?.data?.link
                }
                isObserver = false
            }
        }

        viewModel.brandListResult.observe(viewLifecycleOwner) { response ->
            if (isBrandObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                brandList.clear()
                brandList.addAll(response.data)
                filterBrandAdapter?.notifyDataSetChanged()
                isBrandObserver = false
            }
        }

        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isCategoryObserever) {
                ProgressDialogUtils.getInstance().hideProgress()
                categoryList.clear()
                categoryList.addAll(response.data)
                filterCategoryAdapter?.notifyDataSetChanged()
                isCategoryObserever = false
            }
        }

        viewModel.sizeListResult.observe(viewLifecycleOwner) { response ->
            if (isSizeObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                sizeList.clear()
                sizeList.addAll(response.data)
                filterSizeAdapter?.notifyDataSetChanged()
                isSizeObserver = false
            }
        }

        viewModel.colorListResult.observe(viewLifecycleOwner) { response ->
            if (isColorObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                colorList.clear()
                colorList.addAll(response.data)

                newColorList.clear()
                if (colorList.size>10){
                    newColorList.addAll(colorList.take(10))
                }
                else{
                    newColorList.addAll(colorList)
                }

                filterColorAdapter?.notifyDataSetChanged()
                isColorObserver = false
            }
        }

        viewModel.affiliateProductSearchResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    productList.clear()
                    productList.addAll(response.data.data)
                    productAdapter?.notifyDataSetChanged()
                    checkData()
                }
                isObserver = false
            }
        }

        viewModel.getAffiliateSorListResult.observe(viewLifecycleOwner) { response ->
            if (isSortObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                sortList.clear()
                sortList.addAll(response.data)
                sortAdapter?.notifyDataSetChanged()
                isSortObserver = false
            }
        }

    }


    private fun getStoreSortList(){
        viewModel.getAffiliateSorList()
        isSortObserver = true
    }
    private fun affiliateProductSearch() {
        val map = HashMap<String,Any>()

        if (sortFilter!=null) {
            map["sort_filter"] = sortFilter.toString()
        }
        if (lastQuery!=null&&lastQuery!="null"&&lastQuery!="") {
            map["search"] = lastQuery.toString()
        }
        if(searchList.isNotEmpty()){
            map["attributes_data"] = searchList
        }

        if (selectedCategory!=null) {
            map["category_id"] = arrayListOf(selectedCategory?.id.toString())    // ArrayList<String>
        }

        if (selectedBrandList.isNotEmpty()) {
            map["brand_id"] = selectedBrandList.map { it.id }
        }

        if (selectedMinPrice != 0 && selectedMaxPrice != 0) {
            map["price_min"] = selectedMinPrice!!
            map["price_max"] = selectedMaxPrice!!
        }

        if (selectedColorList.isNotEmpty()) {
            map["color_id"] = selectedColorList.map { it.id }
        }
        if (selectedSizeList.isNotEmpty()) {
            map["size_id"] = selectedSizeList.map { it.id }
        }

        if (selectedDiscount!=0&&selectedDiscount!=null) {
            map["discount_range"] = selectedDiscount.toString()
        }

        viewModel.affiliateProductSearch(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }


    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = productList.isEmpty()
    }

    private fun sortProductDialog() {
        var sort = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SortProductBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            for (i in 0 until sortList.size) {
                if (sortList[i].id.toString() == sortFilter) {
                    sortList[i].isSelected = true
                }
                else{
                    sortList[i].isSelected = false
                }
            }

            sortAdapter = SortListAdapter(requireActivity(),sortList,object:SortListAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    sort = sortList[position].id.toString()
                }

            })

            rvSort.adapter = sortAdapter

            if (sortList.size==0) {
                getStoreSortList()
            }

            tvClear.setOnClickListener {
                sortFilter = null
                affiliateProductSearch()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (sort!=""){
                    sortFilter = sort
                    affiliateProductSearch()
                    dialog.dismiss()
                }
                else{
                    showToast("Please select")
                }


            }

        }

        dialog.show()
    }

    private fun productFilterDialog() {
        var category : GetCategoryResponse.Data? = null
        var min: Int = 0
        var max: Int = 0

        var discount: Int? = null
        var rating: Int? = null
        var deliveryTime: String? = null

        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ProductFilterBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        dialogBinding.apply {

            brandList.forEach { it.isSelected = false }
            colorList.forEach { it.isSelected = false }
            sizeList.forEach { it.isSelected = false }

            if (selectedMaxPrice!=0) {
                tvCentrePrice.text = "$selectedMaxPrice KD"
            }

            when (selectedDiscount) {
                10 -> radioGroupDiscount.check(R.id.rb10)
                20 -> radioGroupDiscount.check(R.id.rb20)
                30 -> radioGroupDiscount.check(R.id.rb30)
                40 -> radioGroupDiscount.check(R.id.rb40)
                50 -> radioGroupDiscount.check(R.id.rb50)
            }

            when (selectedRating) {
                1 -> radioGroupRating.check(R.id.rb1Star)
                2 -> radioGroupRating.check(R.id.rb2Star)
                3 -> radioGroupRating.check(R.id.rb3Star)
                4 -> radioGroupRating.check(R.id.rb4Star)
                5 -> radioGroupRating.check(R.id.rb5Star)
            }

            when (selectedDeliveryTime) {
                "Today-2 days" -> rgDeliveryTime.check(R.id.rb2Day)
                "3-5 days" -> rgDeliveryTime.check(R.id.rb3day)
                "6-12 days" -> rgDeliveryTime.check(R.id.rb6day)
            }

            if (selectedBrandList.isNotEmpty()) {
                brandList.forEach { brand ->
                    brand.isSelected = selectedBrandList.any { it.id == brand.id }
                }
            }

            if (selectedColorList.isNotEmpty()) {
                colorList.forEach { color ->
                    color.isSelected = selectedColorList.any { it.id == color.id }
                }
            }


            if (selectedSizeList.isNotEmpty()) {
                sizeList.forEach { size ->
                    size.isSelected = selectedSizeList.any { it.id == size.id }
                }
            }


            newColorList.clear()
            if (colorList.size>10){
                newColorList.addAll(colorList.take(10))
            }
            else{
                newColorList.addAll(colorList)
            }

            filterColorAdapter = FilterColorAdapter(requireActivity(),newColorList,object :FilterColorAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    colorList[position].isSelected = newColorList[position].isSelected
                }

            })

            rvColor.adapter = filterColorAdapter

            filterBrandAdapter = FilterBrandAdapter(requireActivity(), brandList,object :FilterBrandAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvBrand.adapter = filterBrandAdapter

            filterSizeAdapter = FilterSizeAdapter(requireActivity(), sizeList,object :FilterSizeAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })
            rvSize.adapter = filterSizeAdapter

            filterCategoryAdapter = FilterCategoryAdapter(requireActivity(), categoryList,object :FilterCategoryAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    category = categoryList[position]
                }

            })
            filterCategoryAdapter?.setSelectedCategoryId(selectedCategory?.id)
            rvCategory.adapter = filterCategoryAdapter

            if(colorList.isEmpty()||sizeList.isEmpty()||brandList.isEmpty()){
                getAllFilterList()
            }

            priceSlider.addOnChangeListener { slider, _, _ ->
                val values = slider.values

                min = values[0].toInt()
                max = values[1].toInt()

                tvMinPrice.text = "$min KD"
                tvCentrePrice.text = "$max KD"

                slider.post {
                    //  moveLabel(tvMinPrice, slider, 0)
                    if(max==100||max==100000){
                        tvCentrePrice.isVisible = false
                    }
                    else{
                        tvCentrePrice.isVisible = true
                    }
                    moveLabel(tvCentrePrice, slider, 1)
                }
            }

            radioGroupDiscount.setOnCheckedChangeListener { _, checkedId ->
                discount = when (checkedId) {
                    R.id.rb10 -> 10
                    R.id.rb20 -> 20
                    R.id.rb30 -> 30
                    R.id.rb40 -> 40
                    R.id.rb50 -> 50
                    else -> null
                }
            }

            radioGroupRating.setOnCheckedChangeListener { _, checkedId ->
                rating = when (checkedId) {
                    R.id.rb1Star -> 1
                    R.id.rb2Star -> 2
                    R.id.rb3Star -> 3
                    R.id.rb4Star -> 4
                    R.id.rb5Star -> 5
                    else -> null
                }
            }

            rgDeliveryTime.setOnCheckedChangeListener { _, checkedId ->
                deliveryTime = when (checkedId) {
                    R.id.rb2Day -> "2"
                    R.id.rb3day -> "3"
                    R.id.rb6day -> "6"
                    else -> null
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                selectedCategory = null
                selectedMinPrice = 0
                selectedMaxPrice = 0
                selectedDiscount = null
                selectedRating = null
                selectedDeliveryTime = null
                selectedSizeList.clear()
                selectedColorList.clear()
                selectedBrandList.clear()
                affiliateProductSearch()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                selectedMaxPrice = min
                selectedMinPrice = max
                selectedCategory = category
                selectedDeliveryTime = deliveryTime
                selectedRating = rating
                selectedDiscount = discount
                selectedBrandList.clear()
                for(i in 0 until brandList.size){
                    if (brandList[i].isSelected) {
                        selectedBrandList.add(brandList[i])
                    }
                }
                for(i in 0 until sizeList.size){
                    if (sizeList[i].isSelected) {
                        selectedSizeList.add(sizeList[i])
                    }
                }
                for(i in 0 until colorList.size){
                    if (colorList[i].isSelected) {
                        selectedColorList.add(colorList[i])
                    }
                }

                affiliateProductSearch()
                dialog.dismiss()
            }

            tvShowMoreColor.setOnClickListener {
                if(tvShowMoreColor.text=="Show More Color"){
                    newColorList.clear()
                    newColorList.addAll(colorList)
                    filterColorAdapter?.notifyDataSetChanged()
                    tvShowMoreColor.text = "Show Less Color"
                }
                else{
                    newColorList.clear()
                    if (colorList.size>10){
                        newColorList.addAll(colorList.take(10))
                    }else {
                        newColorList.addAll(colorList)
                    }
                    filterColorAdapter?.notifyDataSetChanged()
                    tvShowMoreColor.text = "Show More Color"
                }

            }

        }

        dialog.show()
    }

    private fun moveLabel(label: TextView, slider: RangeSlider, index: Int) {
        val thumbX = slider.thumbRadius * 2 +
                (slider.width - slider.thumbRadius * 4) * slider.values[index] /
                (slider.valueTo - slider.valueFrom)

        label.x = thumbX - label.width / 2f
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

    private fun getAllFilterList() {
        viewModel.getColorList()
        viewModel.getBrandList()
        viewModel.getCategory()
        viewModel.getSizeList()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isBrandObserver = true
        isCategoryObserever = true
        isSizeObserver = true
        isColorObserver = true
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
                Log.e("VISION_RESPONSE_123", response.body().toString() ?: "Error")
                val labels = response.body()?.responses
                    ?.firstOrNull()
                    ?.labelAnnotations
                    ?.map { it.description }
                    ?: emptyList()
                searchList = ArrayList(labels)
                affiliateProductSearch()

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

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun addAffiliateProduct() {
        val map = HashMap<String, Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addAffiliateProduct(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getAffiliateProductLink() {
        val map = HashMap<String, Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.getAffiliateProductLink(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

}
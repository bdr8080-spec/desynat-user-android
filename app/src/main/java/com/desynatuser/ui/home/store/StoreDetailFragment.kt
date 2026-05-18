package com.desynatuser.ui.home.store

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.adapter.FilterBrandAdapter
import com.desynatuser.adapter.FilterCategoryAdapter
import com.desynatuser.adapter.FilterColorAdapter
import com.desynatuser.adapter.FilterSizeAdapter
import com.desynatuser.adapter.HighlightAdapter
import com.desynatuser.adapter.ProductAdapter
import com.desynatuser.adapter.ReelAdapter
import com.desynatuser.adapter.SortListAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentStoreDetailBinding
import com.desynatuser.databinding.ProductFilterBinding
import com.desynatuser.databinding.SortProductBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.loadBase64Image
import com.desynatuser.utils.showToast
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreDetailFragment : BaseFragment() {
    private var binding : FragmentStoreDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isReelObserver = false
    var isStoryObserver = false
    var isHighlightObserver = false

    private var storyList= ArrayList<GetSellerStoryResponse.Data>()
    private var highlightList= ArrayList<GetSellerStoryResponse.Data>()
    private var reelList= ArrayList<GetSellerStoryResponse.Data>()
    var isSortObserver = false

    private var reelAdapter: ReelAdapter? = null

    private var highlightAdapter: HighlightAdapter? = null
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    private var sortList = ArrayList<GetStoreSortListResponse.Data>()
   // private var storeData: GetStoreResponse.Data?=null
    private var storeDetailData: GetSingleStoreResponse.Data?=null
    var selectedPos = -1
    var selectedReelPos = -1
    private var sortFilter: String?=null
    private var type = 1 //1-product.2-reel
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
    var isSizeObserver = false
    var isColorObserver = false
    val newColorList = ArrayList<GetAllColorListResponse.Data>()
    private var isFirstLoad = true
    private var storeId = ""
    private var subCategoryData: GetSubCategoryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailBinding.inflate(layoutInflater, container,false)
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
        categoryList.clear()
        SharedPreference.get(requireActivity()).getCategoryList()?.let { categoryList.addAll(it) }
        ProgressDialogUtils.getInstance().hideProgress()
         storeId = arguments?.getString("storeId").toString()
        val itemJson = arguments?.getString("subCategoryData")
        if (!itemJson.isNullOrEmpty()) {
            subCategoryData = Gson().fromJson(itemJson, GetSubCategoryResponse.Data::class.java)
        }
        binding?.apply {

            when(type){
                1->{
                    product()
                }
                2->{
                    reel()
                }
            }

            productAdapter = ProductAdapter(requireActivity(),productList,storeDetailData?.store?.storeName.toString(),3,object: ProductAdapter.ProductClick{
                override fun onProductClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                                putString("productId", productList[position].id.toString())
                            }
                            findNavController().navigate(R.id.action_storeDetailFragment_to_productDetailFragment,bundle)
                        }

                        2->{
                            selectedPos = position
                           addToWishList()
                        }
                    }

                }

            })
            rvProduct.adapter = productAdapter

            highlightAdapter = HighlightAdapter(requireActivity(), highlightList,object :
                HighlightAdapter.OnClick{
                override fun onItemClick(position: Int) {
                    if (highlightList.size!=0) {
                        val list = ArrayList<GetSellerStoryResponse.Data>()
                        list.add(highlightList[position])
                        val bundle = Bundle().apply {
                            putString("from", "highlights")
                            putString("sellerId", storeDetailData?.store?.sellerId.toString())
                            putString("storyData", Gson().toJson(highlightList))
                        }
                        findNavController().navigate(
                            R.id.action_storeDetailFragment_to_viewStoryFragment,
                            bundle
                        )
                    }
                }

            })
            rvHighlights.adapter = highlightAdapter

            reelAdapter = ReelAdapter(requireActivity(), reelList,2,object :
                ReelAdapter.OnClick{
                override fun onItemClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            if (reelList.size!=0) {
                                val bundle = Bundle().apply {
                                    putString("from", "reel")
                                    putString("sellerId", reelList[position]?.sellerId.toString())
                                    putString("storyData", Gson().toJson(reelList[position]))
                                }
                                findNavController().navigate(
                                    R.id.action_storeDetailFragment_to_viewReelFragment,
                                    bundle
                                )
                            }
                        }
                        2->{
                            selectedReelPos = position
                            addRemoveReelToWishlist()
                        }
                        3->{
                            selectedReelPos = position
                            shareReel()
                        }
                    }

                }

            })
            rvReel.adapter = reelAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            getStoreDetail(isLoading = false)
                    }

                }
            })

        }

        getStoreDetail()
//        if (isFirstLoad) {
//            getStoreDetail()
//            isFirstLoad = false
//        }

    }

    private fun setData() {
        binding?.apply {
            tvToolbar.text = storeDetailData?.store?.storeName
            tvBrand.text = storeDetailData?.store?.storeName
            tvDescription.text = storeDetailData?.store?.bio
            if (storeDetailData?.store?.averageStoreRating!=null){
                tvRating.text = "(${storeDetailData?.store?.averageStoreRating})"
            }
            else{
                tvRating.text = "(0)"
            }
            ratingBar.rating = storeDetailData?.store?.averageStoreRating?.toFloatOrNull()?:0f
            tvName.text = storeDetailData?.store?.sellerInformation?.firstName+" "+storeDetailData?.store?.sellerInformation?.lastName

            ivProfileImage.load(storeDetailData?.store?.sellerInformation?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            Glide.with(requireActivity()).load(storeDetailData?.store?.storeImage).placeholder(R.color.black).into(ivStore)

            if (storeDetailData?.store?.sellerInformation?.profileQr != null) {

                val imageLoader = ImageLoader.Builder(requireContext())
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()

                ivQr.load(storeDetailData?.store?.sellerInformation?.profileQr, imageLoader) {
                    crossfade(true)
                    placeholder(R.drawable.qr)
                    error(R.drawable.qr)
                }
            }

            if (storeDetailData?.store?.storeAvailability==1){
                tvStatus.isVisible = false
                tvStatus.text = getString(R.string.available)
            }
            else{
                tvStatus.text = getString(R.string.busy)
                tvStatus.isVisible = true
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivShare.setOnClickListener {
                CommonMethod.shareProductWithImage(requireActivity(),
                    "Store name: ${storeDetailData?.store?.storeName}",
                    "Bio: ${storeDetailData?.store?.bio}",
                    storeDetailData?.store?.storeImage ?: "",
                    storeDetailData?.store?.sellerInformation?.qrSellerUrl ?: ""
                )
            }

            ivProfileImage.setOnClickListener {
                if (storyList.size!=0) {
                    val bundle = Bundle().apply {
                        putString("from", "story")
                        putString("sellerId", storeDetailData?.store?.sellerId.toString())
                        putString("storyData", Gson().toJson(storyList))
                    }
                    findNavController().navigate(
                        R.id.action_storeDetailFragment_to_viewStoryFragment,
                        bundle
                    )
                }
            }

            tvFilter.setOnClickListener {
                productFilterDialog()
            }

            tvSort.setOnClickListener {
                sortProductDialog()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }

            tvProduct.setOnClickListener {
              product()
            }

            tvReel.setOnClickListener {
               reel()
            }

            ivChat.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    chatWithSeller()
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }
        }
    }

    private fun product(){
        binding?.apply {
            type = 1
            tvProduct.setBackgroundResource(R.drawable.selected_left_tab)
            tvProduct.setTextColor(requireActivity().getColor(R.color.white))
            tvReel.setTextColor(requireActivity().getColor(R.color.black))
            tvReel.background = null
            clProduct.isVisible = true
            rvReel.isVisible = false
            checkData()
        }
    }

    private fun reel(){
        binding?.apply {
            type =2
            tvProduct.background = null
            tvProduct.setTextColor(requireActivity().getColor(R.color.black))
            tvReel.setTextColor(requireActivity().getColor(R.color.white))
            tvReel.setBackgroundResource(R.drawable.selected_right_tab)
            clProduct.isVisible = false
            rvReel.isVisible = true
        }

    }
    private fun chatWithSeller(){
        val map = HashMap<String, Any>()
        map["seller_id"] = storeDetailData?.store?.sellerId.toString()
        viewModel.chatWithSeller(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }


    private fun getStoreSortList(){
        viewModel.getStoreSorList()
        isSortObserver = true
    }

    private fun getStoreDetail(isLoading: Boolean=true){
        val map = HashMap<String,Any>()
        map["store_id"] = storeId
        if (sortFilter!=null) {
            map["sort_filter"] = sortFilter.toString()
        }
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }

        if (selectedCategory!=null) {
            map["category_id"] = arrayListOf(selectedCategory?.id.toString())    // ArrayList<String>
        }

        if (subCategoryData!=null&&selectedCategory==null) {
            map["category_id"] = arrayListOf(subCategoryData?.categoryId.toString())    // ArrayList<String>
        }

        if (selectedBrandList.isNotEmpty()) {
            map["brand_id"] = selectedBrandList.map { it.id }
        }

        if (selectedMaxPrice != 0) {
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
        if (selectedRating!=0&&selectedRating!=null) {
            map["rating"] = selectedRating.toString()
        }
        if (selectedDeliveryTime!=""&&selectedDeliveryTime!=null) {
            map["delivery_time"] = selectedDeliveryTime.toString()
        }

        viewModel.getSingleStoreInfo(map)
        if (isLoading){
            binding?.progressBar?.isVisible = true
        }
        isObserver = true
    }

    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addToWishlist(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun addRemoveReelToWishlist(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelList[selectedReelPos].id
        viewModel.addRemoveReelToWishlist(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun shareReel(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelList[selectedReelPos].id
        viewModel.shareReel(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getAllFilterList() {
        viewModel.getColorList()
        viewModel.getBrandList()
        viewModel.getSizeList()
        binding?.progressBar?.isVisible = true
        isBrandObserver = true
        isSizeObserver = true
        isColorObserver = true
    }

    private fun getSellerStory(){
        val map = HashMap<String, Any>()
        map["seller_id"]= storeDetailData?.store?.sellerId.toString()
        viewModel.getSellerStory(map)
        isStoryObserver = true
    }

    private fun getSellerReel(){
        val map = HashMap<String, Any>()
        map["seller_id"]= storeDetailData?.store?.sellerId.toString()
        viewModel.getSellerReel(map)
        isReelObserver = true
    }

    private fun getSellerHighlights(){
        val map = HashMap<String, Any>()
        map["seller_id"]= storeDetailData?.store?.sellerId.toString()
        viewModel.getSellerHighlights(map)
        isHighlightObserver = true
    }

    private fun captureSellerStoreVisitor(sellerId: String){
        val map = HashMap<String,Any>()
        map["seller_id"] = sellerId
        map["store_id"] = storeId
        if (!Constants.isGuest) {
            map["user_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
        }
        viewModel.captureSellerStoreVisitor(map)
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isStoryObserver||isReelObserver||isHighlightObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                errorMessage?.let { showToast(it) }
                isObserver = false
                isStoryObserver = false
                isReelObserver = false
                isHighlightObserver = false
            }
        }

        viewModel.brandListResult.observe(viewLifecycleOwner) { response ->
            if (isBrandObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                brandList.clear()
                brandList.addAll(response.data)
                filterBrandAdapter?.notifyDataSetChanged()
                isBrandObserver = false
            }
        }

        viewModel.sizeListResult.observe(viewLifecycleOwner) { response ->
            if (isSizeObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                sizeList.clear()
                sizeList.addAll(response.data)
                filterSizeAdapter?.notifyDataSetChanged()
                isSizeObserver = false
            }
        }

        viewModel.colorListResult.observe(viewLifecycleOwner) { response ->
            if (isColorObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
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

        viewModel.storeSortResult.observe(viewLifecycleOwner) { response ->
            if (isSortObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                sortList.clear()
                sortList.addAll(response.data)
                sortAdapter?.notifyDataSetChanged()
                isSortObserver = false
            }
        }

        viewModel.storeDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                storeDetailData = response.data
                getSellerHighlights()
                getSellerStory()
                getSellerReel()
                setData()
                captureSellerStoreVisitor(response?.data?.store?.sellerId.toString())
                setProductData()
                isObserver = false
            }
        }

        viewModel.captureSellerStoreVisitorResult.observe(viewLifecycleOwner) { response ->

        }

        viewModel.chatWithSellerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                val bundle = Bundle().apply {
                    putString("from", "store")
                    putString("normalChatData", Gson().toJson(response.data))
                }
                findNavController().navigate(R.id.action_storeDetailFragment_to_chatFragment,bundle)
                isObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
        if (isObserver) {
            ProgressDialogUtils.getInstance().hideProgress()
            binding?.progressBar?.isVisible = false

            if (productList[selectedPos].wishlist==1){
                showToast(getString(R.string.product_removed_from_your_wishlist_successfully))
                productList[selectedPos].wishlist = 0
            }
            else{
                showToast(getString(R.string.product_added_to_your_wishlist_successfully))
                productList[selectedPos].wishlist = 1
            }

            productAdapter?.notifyItemChanged(selectedPos)
            selectedPos = -1
            isObserver = false
        }

    }

        viewModel.addRemoveReelToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (reelList[selectedReelPos].wishlist==1){
                    showToast(getString(R.string.reel_removed_from_your_wishlist_successfully))
                    reelList[selectedReelPos].wishlist = 0
                }
                else{
                    showToast(getString(R.string.reel_added_to_your_wishlist_successfully))
                    reelList[selectedReelPos].wishlist = 1
                }

                reelAdapter?.notifyItemChanged(selectedReelPos)
                selectedReelPos = -1
                isObserver = false
            }

        }

        viewModel.getSellerReelResult.observe(viewLifecycleOwner) { response ->
            if (isReelObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    reelList.clear()
                    reelList.addAll(response.data)
                    reelAdapter?.notifyDataSetChanged()

                }
                isReelObserver = false
            }
        }


        viewModel.getSellerHighlightsResult.observe(viewLifecycleOwner) { response ->
            if (isHighlightObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    highlightList.clear()
                    highlightList.addAll(response.data)
                    highlightAdapter?.notifyDataSetChanged()

                }
                isHighlightObserver = false
            }
        }


        viewModel.getSellerStoryResult.observe(viewLifecycleOwner) { response ->
            if (isStoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    storyList.clear()
                    storyList.addAll(response.data)
                    setStory()

                }
                isStoryObserver = false
            }
        }

        viewModel.shareReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                val fileUrl = reelList[selectedReelPos]?.file ?: ""

                val isVideo = fileUrl.contains(".mp4", true) ||
                        fileUrl.contains(".mov", true) ||
                        fileUrl.contains(".mkv", true)
                CommonMethod.shareReel(requireActivity(),
                    caption = reelList[selectedReelPos]?.caption ?: "",
                    mediaUrl = fileUrl,
                    link = response.data.file ?: "",
                    isVideo = isVideo

                )
                selectedPos = -1
                isObserver = false
            }
        }
    }


    private fun setStory(){
        binding?.apply {
         //   ivProfileImage.segmentCount = storyList.size
            if (storyList.size!=0){
                ivStoryHighlight.isVisible = true
            }
            else{
                ivStoryHighlight.isInvisible = true
            }
        }
    }

    private fun setProductData() {
        binding?.apply {
            productList.clear()
            storeDetailData?.allProduct?.data?.let { productList.addAll(it) }
            checkData()
            productAdapter?.updateStoreName(storeDetailData?.store?.storeName?:"")
        }
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
                getStoreDetail()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (sort!=""){
                    sortFilter = sort
                    getStoreDetail()
                    dialog.dismiss()
                }
                else{
                    showToast(getString(R.string.please_select))
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

            tvMinPrice.text = "0 ${CommonMethod.getCurrency(requireActivity())}"
            tvMaxPrice.text = "1000 ${CommonMethod.getCurrency(requireActivity())}"


            if (selectedMaxPrice!=0) {
                tvCentrePrice.text = "$selectedMaxPrice ${CommonMethod.getCurrency(requireActivity())}"
            }
            else{
                tvCentrePrice.text = "500 ${CommonMethod.getCurrency(requireActivity())}"
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
                "2" -> rgDeliveryTime.check(R.id.rb2Day)
                "3" -> rgDeliveryTime.check(R.id.rb3day)
                "6" -> rgDeliveryTime.check(R.id.rb6day)
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

                tvMinPrice.text = "$min ${CommonMethod.getCurrency(requireActivity())}"
                tvCentrePrice.text = "$max ${CommonMethod.getCurrency(requireActivity())}"

                slider.post {
                  //  moveLabel(tvMinPrice, slider, 0)
                    if(max==0||max==1000){
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
                getStoreDetail()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                selectedMaxPrice = max
                    selectedMinPrice = min
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

                getStoreDetail()
                dialog.dismiss()
            }

            tvShowMoreColor.setOnClickListener {
                if(tvShowMoreColor.text==getString(R.string.show_more_color)){
                    newColorList.clear()
                    newColorList.addAll(colorList)
                    filterColorAdapter?.notifyDataSetChanged()
                    tvShowMoreColor.text = getString(R.string.show_less_color)
                }
                else{
                    newColorList.clear()
                    if (colorList.size>10){
                        newColorList.addAll(colorList.take(10))
                    }else {
                        newColorList.addAll(colorList)
                    }
                    filterColorAdapter?.notifyDataSetChanged()
                    tvShowMoreColor.text = getString(R.string.show_more_color)
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
            showToast(getString(R.string.speech_recognition_not_supported))
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

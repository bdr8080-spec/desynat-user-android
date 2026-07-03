package com.desynatuser.ui.home.store

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.SizeAdapter
import com.desynatuser.adapter.ImagePreviewAdapter
import com.desynatuser.adapter.ProductColorAdapter
import com.desynatuser.adapter.ProductImageAdapter
import com.desynatuser.adapter.ReviewAdapter
import com.desynatuser.adapter.SimilarProductAdapter
import com.desynatuser.adapter.SpecificationAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.ImagePreviewDialogBinding
import com.desynatuser.databinding.SizeGuideChartDialogBinding
import com.desynatuser.ui.home.store.response.AddToCartListResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetProductDetailResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.Product
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment() {
    private var binding : FragmentProductDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isCartObserver = false
    var isProductObserver = false
    private var productData :GetSingleStoreResponse.Data.AllProduct.Data?=null
    private var productDetailData :GetProductDetailResponse.Data?=null
    private var imageList = ArrayList<String>()
    private var colorList = ArrayList<Product>()
    private var sizeList = ArrayList<Product.ProductVariation>()
    private var productImageAdapter: ProductImageAdapter?=null
    private var colorAdapter: ProductColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var similarProductAdapter: SimilarProductAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var specificationList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductSpecification>()

    private var reviewAdapter: ReviewAdapter?=null
    private var selectedPos = 0
    private var selectedSizePos = 0
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var similarProductList = ArrayList<GetProductDetailResponse.Data.SimilarProduct>()
    private var cartList = ArrayList<AddToCartListResponse.Data>()
    var productInfo = ArrayList<Product>()
    var ratingList = ArrayList<GetProductDetailResponse.Data.Review>()

    var cartId:String? = null
    var productId:String? = null
    var refLink:String? = null
    var refId:String? = null
    var cartQuantity = 0
    var cartData : AddToCartListResponse.Data?=null

    var fromBtn = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentProductDetailBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        productId = arguments?.getString("productId")
        if (arguments?.getString("ref_link")!=null){
            refLink = arguments?.getString("ref_link")
            refId = arguments?.getString("ref_id")
        }

        val itemJson = arguments?.getString("productData")
        if (!itemJson.isNullOrEmpty()) {
            productData = Gson().fromJson(itemJson, GetSingleStoreResponse.Data.AllProduct.Data::class.java)
            productInfo = productData?.productVariants?.let { mapVariantsToProducts(it) }!!
        }
        binding?.tvDeliveryDate?.text = if (Constants.language=="ar") productData?.delivery_time?.name_ar else  productData?.delivery_time?.name


        similarProductAdapter = SimilarProductAdapter(requireActivity(), similarProductList,productDetailData?.store?.storeName.toString(),object:SimilarProductAdapter.ProductClick{
            override fun onProductClick(position: Int) {
                productId=similarProductList[position].id.toString()

                getProductDetail()

            }

        })
        binding?.rvSimilarProduct?.adapter = similarProductAdapter


        /*for(i in 0 until (productData?.productVariants?.size ?: 0)){
            productData?.productVariants?.get(i)?.color?.let { colorList.add(it) }
        }*/

        colorAdapter = ProductColorAdapter(requireActivity(), colorList,object :ProductColorAdapter.OnClick{
            override fun onClickItem(position: Int) {
                selectedPos = position
                selectedSizePos = 0
                sizeAdapter?.updateData(selectedSizePos)
                setData()
            }

        })

            binding?.rvColor?.adapter = colorAdapter

        sizeAdapter = SizeAdapter(requireActivity(),
              sizeList,object : SizeAdapter.OnClick{
                    override fun onClickItem(position: Int) {
                        selectedSizePos = position
                        setData()
                    }

                })

        binding?.rvSize?.adapter = sizeAdapter

        specificationAdapter = SpecificationAdapter(requireActivity(),specificationList)
        binding?.rvSpecification?.adapter = specificationAdapter


        imageList.clear()
        productImageAdapter = ProductImageAdapter(requireActivity(),imageList,object:ProductImageAdapter.OnClick{
            override fun onClick(position: Int) {
                imagePreviewDialog(position)
            }

        })
        binding?.viewPager?.adapter = productImageAdapter

        TabLayoutMediator(binding!!.dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

        reviewAdapter = ReviewAdapter(requireActivity(), ratingList)
        binding?.rvRating?.adapter = reviewAdapter
        getProductDetail()
        binding?.tvSearchAddress?.text = SharedPreference.get(requireActivity()).getDefaultAddress()?.address
        if (SharedPreference.get(requireActivity()).accessToken!="") {
            viewCartDetail()
        }
    }

    private fun setData() {
        binding?.apply {
            tvDeliveryDate?.text = "Get within " + if (Constants.language=="ar") productDetailData?.delivery_time?.name_ar else  productDetailData?.delivery_time?.name

            tvBuyCount.text = "0 + ${getString(R.string.bought)}"
            tvTitle.text = productDetailData?.store?.storeName
            tvProductName.text =  if (Constants.language=="ar") productDetailData?.productNameAr+" (${requireActivity().getString(R.string.by)} ${productDetailData?.store?.storeName})" else productDetailData?.productNameEn+" (${requireActivity().getString(R.string.by)} ${productDetailData?.store?.storeName})"
            if (productDetailData?.productAvgRating!=null){
                tvRating.text = "(${productDetailData?.productAvgRating})"
            }
            else{
                tvRating.text = "(0)"
            }
            ratingBar.rating = productDetailData?.productAvgRating?.toFloatOrNull()?:0f
            productInfo = productDetailData?.productVariants?.let { mapVariantsToProducts(it) }!!

            colorList.clear()
            colorList.addAll(productInfo)
            colorAdapter?.notifyDataSetChanged()
            btnCustomOrder.isVisible = productDetailData?.customizedProduct=="1"&&productDetailData?.store?.storeAvailability==1

            if (productDetailData?.wishlist==1) {
                ivLike.setImageResource(R.drawable.ic_heart_filled)
            }
            else{
                ivLike.setImageResource(R.drawable.ic_heart)
            }

            val variant = productInfo?.getOrNull(selectedPos)

            imageList.clear()
            variant?.variantImage?.let { imageList.addAll(it) }
            productImageAdapter?.notifyDataSetChanged()
            viewPager.setCurrentItem(0, false)

            sizeList.clear()
            variant?.productVariation?.let { sizeList.addAll(it) }
            sizeAdapter?.notifyDataSetChanged()

            tvName.text = if (Constants.language=="ar") productDetailData?.brand?.nameArabic else productDetailData?.brand?.nameEnglish

           /* if (variant?.productVariation?.getOrNull(selectedSizePos)?.totalStock?.toIntOrNull()?:0>0&&productDetailData?.store?.storeAvailability==1){
                relAddToBag.isVisible = true
                btnBuyNow.isVisible = true
                viewBottom.isVisible = true
            }
            else{
                relAddToBag.isVisible = false
                btnBuyNow.isVisible = false
                viewBottom.isVisible = false
            }*/


            if (variant?.productVariation?.getOrNull(selectedSizePos)?.totalStock?.toIntOrNull()?:0>0&&productDetailData?.store?.storeAvailability==1){
                relAddToBag.isVisible = true
                btnBuyNow.isVisible = true
                viewBottom.isVisible = true
            }
            else{
                relAddToBag.isVisible = true
                btnBuyNow.isVisible = true
                viewBottom.isVisible = true
            }

            if (productData?.category?.name=="Women"){
                clWomenSize.isVisible = true
                tvBustSize.text = variant?.productVariation?.getOrNull(selectedSizePos)?.bust?:"NA"
                tvHipSize.text =variant?.productVariation?.getOrNull(selectedSizePos)?.hip?:"NA"
                tvShoulderSize.text = variant?.productVariation?.getOrNull(selectedSizePos)?.shoulder?:"NA"
                tvWaistSize.text = variant?.productVariation?.getOrNull(selectedSizePos)?.waist?:"NA"
                tvSleeveLengthSize.text = variant?.productVariation?.getOrNull(selectedSizePos)?.sleeveLength?:"NA"
                tvLengthSize.text = variant?.productVariation?.getOrNull(selectedSizePos)?.length?:"NA"

            }
            else{
                clWomenSize.isVisible = false
            }

            tvPrice.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),variant?.productVariation?.getOrNull(selectedSizePos)?.actualPrice?:0.0)}"
            tvDiscountedPrice.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),variant?.productVariation?.getOrNull(selectedSizePos)?.finalPrice?:0.0)}"

            val discountValue = "${CommonMethod.formatCurrencyAmountWithout(requireActivity(),variant?.productVariation?.getOrNull(selectedSizePos)?.discountValue?.toDoubleOrNull() ?: 0.0)}"
            val discountType = if (variant?.productVariation?.getOrNull(selectedSizePos)?.discountType == "1")"%  ${
                requireActivity().getString(
                    R.string.off
                )
            }" else " ${CommonMethod.getCurrency(requireActivity())}  ${requireActivity().getString(R.string.off)}"
            tvOffer.text = "$discountValue$discountType"

            tvDescription.text = if (Constants.language=="ar")  productDetailData?.descriptionAr else productDetailData?.descriptionEn

            specificationList.clear()

            productDetailData?.productSpecifications?.let { specificationList.addAll(it) }
            specificationAdapter?.notifyDataSetChanged()

            tvCashOnDelivery.text = if (productDetailData?.cashOnDelivery=="1")
            {
               getString(R.string.cash_on_delivery_available)
            }
                else {
                getString(R.string.cash_on_delivery_not_available)
                }

            val returnDays = productDetailData?.returnDays
            val exchangeDays = productDetailData?.exchangableDays

            val returnText = returnDays?.let { "$it ${getString(R.string.days_return)}" }
            val exchangeText = exchangeDays?.let { "$it ${getString(R.string.says_exchange)}" }

            val finalText = listOfNotNull(returnText, exchangeText).joinToString(" & ")

            if (finalText.isEmpty()) {
                tvExchange.text = getString(R.string.this_product_is_sold_under_a_no_return_policy)
            } else {
                tvExchange.visibility = View.VISIBLE
                tvExchange.text = finalText
            }

            Log.e("cart", "product: ${productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id}" )
            Log.e("cart", "cart: ${cartData?.variantsId}" )

            var matched = false
            for (i in 0 until cartList.size){
                if(productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id==cartList[i].variantsId){
                    matched = true
                    cartData = cartList[i]
                    cartId = cartData?.id.toString()
                    cartQuantity = cartData?.quantity?:0
                    Log.e("cart", "cartData: ${cartData}" )
                    Log.e("cart", "cart: ${cartData?.variantsId}" )
                    checkCartData(cartData?.quantity?:0)
                }
            }
            if (matched==false) {
                cartId = null
                cartQuantity = 0
                checkCartData(0)
            }

        }
    }

    override fun initControl() {
        binding?.apply {

            relSearch.setOnClickListener{
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    val bundle = Bundle().apply {
                        putString("from", "productDetail")
                    }
                    findNavController().navigate(R.id.action_productDetailFragment_to_savedAddressFragment,bundle)
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }

            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivShare.setOnClickListener {
                CommonMethod.shareProductWithImage(requireActivity(),
                    "${getString(R.string.product_name)}: ${productDetailData?.productNameEn}",
                    "${getString(R.string.description)}: ${productDetailData?.descriptionEn}",
                    productDetailData?.productVariants?.getOrNull(selectedPos)?.variantImage?.getOrNull(0) ?: "",
                    "https://desynat.com/user/product/detail-page/${productId}"
                )
            }

            tvSizeGuide.setOnClickListener {
                sizeGuideDialog()
            }

            btnAddToBag.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    if (selectedSizePos == -1) {
                        showToast(getString(R.string.please_select_size))
                    } else {
                        addToBag()
                    }
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivMinus.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    addCartQuantity("decrement")
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivPlus.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    addCartQuantity("increment")
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            ivLike.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    addToWishList()
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            btnBuyNow.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    if (selectedSizePos == -1) {
                        showToast(getString(R.string.please_select_size))
                    } else {
                        if (cartId != null) {
                            findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
                        } else {
                            fromBtn = "buy"
                            addToBag()
                        }
                    }
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            btnCustomOrder.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                createCustomOrder()
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            relCartItem.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
               // errorMessage?.let { showToast(it) }
               // isObserver = false
            }
        }

        viewModel.cartDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                binding?.progressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                cartList.clear()
                if (myBagList.isNotEmpty()){
                    for(i in 0 until myBagList.size) {
                        if (myBagList[i].orderType=="1") {
                            cartList.add(
                                AddToCartListResponse.Data(
                                    amount = myBagList[i].amount,
                                    variantsId = myBagList[i].variantsId,
                                    id = myBagList[i].id,
                                    quantity = myBagList[i].quantity,
                                    productId = myBagList[i].productId
                                )
                            )
                        }
                    }
                    if (productDetailData!=null){
                        setData()
                    }
                }
                isCartObserver = false
            }
        }

        viewModel.addToCartResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false

                if (fromBtn=="buy"){
                    findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
                    fromBtn = ""
                }
                else {
                    val data = response.data
                    cartList.add(
                        AddToCartListResponse.Data(
                            amount = data.amount,
                            productId = data.productId,
                            variantsId = data.variantsId,
                            id = data.id,
                            quantity = data.quantity
                        )
                    )
                    setData()
                    showToast(response.message)
                }
                isObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (cartQuantity==1&&response.data.quantity==1){
                    var pos = -1
                    for(i in 0 until cartList.size){
                        if (cartList[i].id==cartId){
                            pos = i

                        }
                    }
                    if (pos!=-1) {
                        cartList.removeAt(pos)
                    }
                    setData()
                }
                else{
                    for(i in 0 until cartList.size){
                        if (cartList[i].id==cartId){
                            cartList[i].quantity = response.data.quantity
                        }
                    }
                    setData()
                }

                isObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (productDetailData?.wishlist==1) {
                    showToast(getString(R.string.product_removed_from_your_wishlist_successfully))
                    productDetailData?.wishlist=0
                    binding?.ivLike?.setImageResource(R.drawable.ic_heart)
                }
                else{
                    showToast(getString(R.string.product_added_to_your_wishlist_successfully))
                    productDetailData?.wishlist=1
                    binding?.ivLike?.setImageResource(R.drawable.ic_heart_filled)
                }
                isObserver = false
            }
        }

        viewModel.createCustomOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                val bundle = Bundle().apply {
                    putString("from", "productDetail")
                    putString("customOrderData",Gson().toJson(response.data))
                }
                findNavController().navigate(R.id.action_productDetailFragment_to_chatFragment,bundle)
                isObserver = false
            }
        }

        viewModel.productDetailResult.observe(viewLifecycleOwner) { response ->
            if (isProductObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                productDetailData = response.data
                ratingList.clear()
                productDetailData?.review?.let { ratingList.addAll(it) }
                reviewAdapter?.notifyDataSetChanged()
                similarProductList.clear()
                productDetailData?.similarProducts?.let { similarProductList.addAll(it) }
                similarProductAdapter?.notifyDataSetChanged()

                if (ratingList.isNotEmpty()){
                    binding?.rvRating?.isVisible = true
                    binding?.tvViewAll?.isVisible = true
                    binding?.tvNoReview?.isVisible = false
                }
                else{
                    binding?.tvNoReview?.isVisible = true
                    binding?.rvRating?.isVisible = false
                    binding?.tvViewAll?.isVisible = false
                }
                setData()

               /* if (quantity != null && quantity > 0) {
                    cartData = response?.data?.addtocart
                    cartQuantity = quantity?:0
                    cartId = response?.data?.addtocart?.id?:0
                    checkCartData(cartQuantity)
                } else {
                    cartQuantity = 0
                }*/

                isProductObserver = false
            }
        }
    }

    private fun checkCartData(quantity: Int) {
        if (productDetailData?.store?.storeAvailability==0) return
        binding?.apply {
            if (quantity > 0) {
                tvCount.text = quantity.toString()
                tvItemCount.text = "(${quantity} ${getString(R.string.item)})"
                btnAddToBag.isVisible = false
                clCartQuantity.isVisible = true
                relCartItem.isVisible = true
            }
            else{
                btnAddToBag.isVisible = true
                clCartQuantity.isVisible = false
                relCartItem.isVisible = false
            }
        }
    }
    private fun addToBag(){
        val map = HashMap<String,Any>()
        map["product_id"] = productId.toString()
        map["quantity"] = 1
        map["variants_id"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()

        if (refLink!=null){
            map["reference_link"] = refLink.toString()
        }
        viewModel.addToCart(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }
    private fun createCustomOrder() {
        val map = HashMap<String, Any>()
        map["product_id"] = productId.toString()
        map["variation_id"] =productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
        viewModel.createCustomOrder(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }
    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productId.toString()
        viewModel.addToWishlist(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }
    private fun addCartQuantity(type:String){
        val map = HashMap<String,Any>()
        map["cart_id"] = cartId.toString()
        map["type"] = type//// increment,decrement
        viewModel.addCartQuantity(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }
    private fun sizeGuideDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SizeGuideChartDialogBinding.inflate(LayoutInflater.from(context))
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

            ivSizeChart.load(productDetailData?.uploadSizeChart) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }
        dialog.show()
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
            val address = data?.getStringExtra("addressH") ?: "N/A"
            val addressH = data?.getStringExtra("addressH") ?: "N/A"

            binding?.tvSearchAddress?.text = addressH

        }
    }

    private fun getProductDetail(){
        val map = HashMap<String,Any>()
        map["product_id"] = productId.toString()
        viewModel.getProductDetailInfo(map)
        isProductObserver = true
    }

    fun mapVariantsToProducts(
        variants: ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant>
    ): ArrayList<Product> {

        return ArrayList(
            variants
                .groupBy { it.colorId }   // group by color_id
                .map { (colorId, listByColor) ->

                    val images = ArrayList(listByColor.first().variantImage)

                    val variationList = ArrayList(
                        listByColor.map { variant ->
                            Product.ProductVariation(
                                id = variant?.id.toString(),
                                productId = variant?.productId.toString(),
                                sizeId = variant?.sizeId.toString(),
                                size = variant?.size?:null,
                                actualPrice = variant?.actualPrice,
                                discountType = variant?.discountType?:"",
                                discountValue = variant?.discountValue?:"",
                                finalPrice = variant?.finalPrice,
                                totalStock = variant?.totalStock.toString(),
                                sku = variant?.sku?:"",
                                skuThresholdValue = variant?.skuThresholdValue ?: "0",
                                bust = variant?.bust,
                                waist = variant?.waist,
                                hip = variant?.hip,
                                shoulder = variant?.shoulder,
                                sleeveLength = variant?.sleeveLength,
                                length = variant?.length,
                                status = variant?.status,
                                createdAt = variant?.createdAt,
                                updatedAt = variant?.updatedAt
                            )
                        }
                    )

                    Product(
                        colorId = colorId,
                        color = listByColor.first().color,
                        variantImage = images,
                        productVariation = variationList,
                        productId = listByColor.first().productId.toString()
                    )
                }
        )
    }

    private fun viewCartDetail(){
        viewModel.viewCartDetail()
        binding?.progressBar?.isVisible = true
        isCartObserver = true
    }

    private fun imagePreviewDialog(position: Int) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener{
                dialog?.dismiss()
            }

            val imagePreviewAdapter = ImagePreviewAdapter(requireActivity(),imageList)
            viewPager?.adapter = imagePreviewAdapter

            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

        }

        dialog?.show()
    }

}
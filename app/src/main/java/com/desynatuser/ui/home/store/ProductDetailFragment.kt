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
import com.desynatuser.adapter.SizeAdapter
import com.desynatuser.R
import com.desynatuser.adapter.ProductColorAdapter
import com.desynatuser.adapter.ProductImageAdapter
import com.desynatuser.adapter.ReviewAdapter
import com.desynatuser.adapter.SimilarProductAdapter
import com.desynatuser.adapter.SpecificationAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.SizeGuideChartDialogBinding
import com.desynatuser.ui.home.store.response.AddToCartListResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetProductDetailResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.Product
import com.desynatuser.utils.CommonMethod
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
    private var storeData :GetStoreResponse.Data?=null
    private var productData :GetSingleStoreResponse.Data.AllProduct.Data?=null
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
    private var cartList = ArrayList<AddToCartListResponse.Data>()
    var productInfo = ArrayList<Product>()

    var cartId:String? = null
    var cartQuantity = 0
    var cartData : AddToCartListResponse.Data?=null

    var fromBtn = ""
    var storeName = ""

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
        val itemJson = arguments?.getString("productData")
        val itemJsonStore = arguments?.getString("storeData")
        if (!itemJson.isNullOrEmpty()) {
            storeData = Gson().fromJson(itemJsonStore, GetStoreResponse.Data::class.java)
            productData = Gson().fromJson(itemJson, GetSingleStoreResponse.Data.AllProduct.Data::class.java)
            productInfo = productData?.productVariants?.let { mapVariantsToProducts(it) }!!
        }

        similarProductAdapter = SimilarProductAdapter(requireActivity(), arrayListOf("",""),storeData?.storeName.toString(),object:SimilarProductAdapter.ProductClick{
            override fun onProductClick(position: Int) {

            }

        })
        binding?.rvSimilarProduct?.adapter = similarProductAdapter

        colorList.clear()
        colorList.addAll(productInfo)
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
        productImageAdapter = ProductImageAdapter(requireActivity(),imageList)
        binding?.viewPager?.adapter = productImageAdapter

        TabLayoutMediator(binding!!.dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

        reviewAdapter = ReviewAdapter(requireActivity(), arrayListOf("",""))
        binding?.rvRating?.adapter = reviewAdapter
        if (storeData==null){
            getProductDetail()
        }
        else{
            storeName = storeData?.storeName.toString()
        }
        setData()
        setStoreData()
        binding?.tvSearchAddress?.text = SharedPreference.get(requireActivity()).getDefaultAddress()?.address


        viewCartDetail()
    }

    private fun setStoreData(){
        binding?.apply {
            tvTitle.text = storeName
            tvProductName.text =  productData?.productNameEn+" (by ${storeName})"
        }
    }

    private fun setData() {
        binding?.apply {

            if (productData?.customizedProduct=="1") {
               btnCustomOrder.isVisible = true
            }
            else{
                btnCustomOrder.isVisible = false
            }


            if (productData?.wishlist==1) {
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

            tvName.text = productData?.brand?.nameEnglish

            if (variant?.productVariation?.get(selectedSizePos)?.totalStock?.toIntOrNull()?:0>0){
                relAddToBag.isVisible = true
                btnBuyNow.isVisible = true
                viewBottom.isVisible = true
            }
            else{
                relAddToBag.isVisible = false
                btnBuyNow.isVisible = false
                viewBottom.isVisible = false
            }

            tvPrice.text = "${CommonMethod.formatPrice(variant?.productVariation?.get(selectedSizePos)?.actualPrice?:0.0)} KD"
            tvDiscountedPrice.text = "${CommonMethod.formatPrice(variant?.productVariation?.get(selectedSizePos)?.finalPrice?:0.0)} KD"

            val discountValue =variant?.productVariation?.get(selectedSizePos)?.discountValue ?: 0
            val discountType = if (variant?.productVariation?.get(selectedSizePos)?.discountType == "1")"% off" else " KD off"
            tvOffer.text = "$discountValue$discountType"


            tvDescription.text = productData?.descriptionEn

            specificationList.clear()

            productData?.productSpecifications?.let { specificationList.addAll(it) }
            specificationAdapter?.notifyDataSetChanged()

            tvDeliveryDate.text = "Get it by 30/11/2025"
            tvCashOnDelivery.text = if (productData?.cashOnDelivery=="1")
            {
                "Cash on Delivery Available"
            }
                else {
                    "Cash on Delivery not Available"
                }


            val returnDays = productData?.returnDays
            val exchangeDays = productData?.exchangableDays

            val returnText = returnDays?.let { "$it Days Return" }
            val exchangeText = exchangeDays?.let { "$it Days Exchange" }

            val finalText = listOfNotNull(returnText, exchangeText).joinToString(" & ")

            if (finalText.isEmpty()) {
                tvExchange.text = "This product is sold under a No Return Policy"
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


            //   tvExchange.text = "${productData?.returnDays} Days Return & ${productData?.exchangableDays} days Exchange"

        }
    }

    override fun initControl() {
        binding?.apply {

            relSearch.setOnClickListener{
                val bundle = Bundle().apply {
                    putString("from", "productDetail")
                }
                findNavController().navigate(R.id.action_productDetailFragment_to_savedAddressFragment,bundle)
           /*     val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)*/
            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivShare.setOnClickListener {
                CommonMethod.shareProduct(requireActivity(),
                    productData?.productNameEn ?: "",
                    productData?.descriptionEn ?: "",
                    productData?.productVariants?.get(selectedPos)?.variantImage?.get(0) ?: ""
                )
            }

            tvSizeGuide.setOnClickListener {
                sizeGuideDialog()
            }

            btnAddToBag.setOnClickListener {
                if (selectedSizePos==-1){
                    showToast("Please select size")
                }
                else {
                    addToBag()
                }
            }

            ivMinus.setOnClickListener {
                addCartQuantity("decrement")
            }

            ivPlus.setOnClickListener {
                addCartQuantity("increment")
            }

            ivLike.setOnClickListener {
                addToWishList()
            }

            btnBuyNow.setOnClickListener {
                if (selectedSizePos==-1){
                    showToast("Please select size")
                }
                else {
                    if (cartId!=null){
                        findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
                    }
                    else{
                        fromBtn = "buy"
                        addToBag()
                    }


                }
            }

            btnCustomOrder.setOnClickListener {

            }

            relCartItem.setOnClickListener {
                findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
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

        viewModel.cartDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                cartList.clear()
                if (myBagList.isNotEmpty()){
                    for(i in 0 until myBagList.size) {
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
                    setData()
                }
                isCartObserver = false
            }
        }


        viewModel.addToCartResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                if (fromBtn=="buy"){
                    findNavController().navigate(R.id.action_productDetailFragment_to_myBagFragment)
                    fromBtn = ""
                }
                else {
                    var data = response.data
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
                    // cartId = response.data.id
                    // checkCartData(response.data.quantity)
                    showToast(response.message)
                }
                isObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
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
//
//                    cartQuantity = 0
//                    cartId = null
//                    checkCartData(cartQuantity)
                }
                else{
//                    cartQuantity = response.data.quantity
//                    checkCartData(response.data.quantity)
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

                if (productData?.wishlist==1) {
                    showToast("Product removed from your wishlist successfully")
                    productData?.wishlist=0
                    binding?.ivLike?.setImageResource(R.drawable.ic_heart)
                }
                else{
                    showToast("Product added to your wishlist successfully")
                    productData?.wishlist=1
                    binding?.ivLike?.setImageResource(R.drawable.ic_heart_filled)
                }
                isObserver = false
            }
        }

        viewModel.productDetailResult.observe(viewLifecycleOwner) { response ->
            if (isProductObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                storeName = response.data.store.storeName
                setStoreData()

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
        binding?.apply {
            if (quantity > 0) {
                tvCount.text = quantity.toString()
                tvItemCount.text = "(${quantity} Item)"
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
        map["product_id"] = productData?.id.toString()
        map["quantity"] = 1
        map["variants_id"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
        viewModel.addToCart(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.addToWishlist(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addCartQuantity(type:String){
        val map = HashMap<String,Any>()
        map["cart_id"] = cartId.toString()
        map["type"] = type//// increment,decrement
        viewModel.addCartQuantity(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
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

            ivSizeChart.load(productData?.uploadSizeChart) {
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
        map["product_id"] = productData?.id.toString()
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
                                id = variant.id.toString(),
                                productId = variant.productId.toString(),
                                sizeId = variant.sizeId.toString(),
                                size = variant.size,
                                actualPrice = variant.actualPrice,
                                discountType = variant.discountType,
                                discountValue = variant.discountValue,
                                finalPrice = variant.finalPrice,
                                totalStock = variant.totalStock.toString(),
                                sku = variant.sku,
                                skuThresholdValue = variant.skuThresholdValue ?: "0",
                                bust = variant.bust,
                                waist = variant.waist,
                                hip = variant.hip,
                                shoulder = variant.shoulder,
                                sleeveLength = variant.sleeveLength,
                                length = variant.length,
                                status = variant.status,
                                createdAt = variant.createdAt,
                                updatedAt = variant.updatedAt
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
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCartObserver = true
    }

}
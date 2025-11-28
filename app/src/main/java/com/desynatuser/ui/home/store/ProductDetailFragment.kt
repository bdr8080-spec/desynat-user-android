package com.desynatuser.ui.home.store

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import com.des.adapter.SizeAdapter
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
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
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
    private var storeData :GetStoreResponse.Data?=null
    private var productData :GetSingleStoreResponse.Data.AllProduct.Data?=null
    private var imageList = ArrayList<String>()
    private var colorList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant.Color>()
    private var sizeList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductVariant.Size>()
    private var productImageAdapter: ProductImageAdapter?=null
    private var colorAdapter: ProductColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var similarProductAdapter: SimilarProductAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var specificationList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductSpecification>()

    private var reviewAdapter: ReviewAdapter?=null
    private var selectedPos = 0
    private var selectedSizePos = -1
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()

    var cartId = 0
    var cartQuantity = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentProductDetailBinding.inflate(layoutInflater, container,false)
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
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("productData")
        val itemJsonStore = arguments?.getString("storeData")
        if (!itemJson.isNullOrEmpty()) {
            storeData = Gson().fromJson(itemJsonStore, GetStoreResponse.Data::class.java)
            productData = Gson().fromJson(itemJson, GetSingleStoreResponse.Data.AllProduct.Data::class.java)
        }

        similarProductAdapter = SimilarProductAdapter(requireActivity(), arrayListOf("",""),storeData?.storeName.toString(),object:SimilarProductAdapter.ProductClick{
            override fun onProductClick(position: Int) {

            }

        })
        binding?.rvSimilarProduct?.adapter = similarProductAdapter

        colorList.clear()
        for(i in 0 until (productData?.productVariants?.size ?: 0)){
            productData?.productVariants?.get(i)?.color?.let { colorList.add(it) }
        }

        colorAdapter = ProductColorAdapter(requireActivity(), colorList,object :ProductColorAdapter.OnClick{
            override fun onClickItem(position: Int) {
                selectedPos = position
                setData()
            }

        })

            binding?.rvColor?.adapter = colorAdapter

        sizeAdapter =   SizeAdapter(requireActivity(),
              sizeList,object :SizeAdapter.OnClick{
                    override fun onClickItem(position: Int) {
                        selectedSizePos = position
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

        setData()

        binding?.tvSearchAddress?.text = SharedPreference.get(requireActivity()).getDefaultAddress()?.address

        viewCartDetail()
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

            val variant = productData?.productVariants?.getOrNull(selectedPos)

            imageList.clear()
            variant?.variantImage?.let { imageList.addAll(it) }
            productImageAdapter?.notifyDataSetChanged()
            viewPager.setCurrentItem(0, false)

            sizeList.clear()
            variant?.size?.let { sizeList.add(it) }
            sizeAdapter?.notifyDataSetChanged()

            tvTitle.text = storeData?.storeName
            tvProductName.text =  productData?.productNameEn+" (by ${storeData?.storeName})"
            tvName.text = productData?.brand?.nameEnglish

            tvPrice.text = "${CommonMethod.formatPrice(variant?.actualPrice?:0.0)} KD"
            tvDiscountedPrice.text = "${CommonMethod.formatPrice(variant?.finalPrice?:0.0)} KD"

            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
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

        viewModel.addToCartResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cartId = response.data.id
                checkCartData(response.data.quantity)
                showToast(response.message)
                isObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (cartQuantity==1&&response.data.quantity==1){
                    cartQuantity = 0
                    cartId = 0
                    checkCartData(cartQuantity)
                }
                else{
                    cartQuantity = response.data.quantity
                    checkCartData(response.data.quantity)
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

        viewModel.cartDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                setData()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                checkCartItem()
                isObserver = false
            }
        }

    }

    private fun checkCartItem() {
        Log.e("cart", "checkCartItem: ${productData?.id}" )
       binding?.apply {
           if (myBagList.isNotEmpty()) {
               for(i in 0 until myBagList.size){
                   if(myBagList[i].productId.toString()==productData?.id.toString()){
                       Log.e("cart", "quantity: ${myBagList[i].quantity}" )
                       cartQuantity = myBagList[i].quantity
                       cartId = myBagList[i].id
                   }
               }

               Log.e("cart", "cartQuantity: ${cartQuantity}" )
               if(cartQuantity!=0){
                   checkCartData(cartQuantity)
               }
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
        map["variants_id"] = productData?.productVariants?.get(selectedPos)?.id.toString()
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
        map["cart_id"] = cartId
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

    private fun viewCartDetail(){
        viewModel.viewCartDetail()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


}
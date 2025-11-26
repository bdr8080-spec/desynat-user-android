package com.desynatuser.ui.home.store

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
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
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
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

    }

    private fun setData() {
        binding?.apply {

            if (productData?.wishlist==1) {
                ivLike.setImageResource(R.drawable.ic_like_filled)
            }
            else{
                ivLike.setImageResource(R.drawable.ic_like)
            }

            val variant = productData?.productVariants?.getOrNull(selectedPos)

            imageList.clear()
            variant?.variantImage?.let { imageList.addAll(it) }
            productImageAdapter?.notifyDataSetChanged()

            sizeList.clear()
            variant?.size?.let { sizeList.add(it) }
            sizeAdapter?.notifyDataSetChanged()

            tvTitle.text = storeData?.storeName
            tvProductName.text =  productData?.productNameEn+" (by ${storeData?.storeName})"
            tvName.text = productData?.brand?.nameEnglish

            tvPrice.text = "${variant?.actualPrice} KD"
            tvDiscountedPrice.text = "${variant?.finalPrice} KD"

            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1") "% off" else "KD off"
            tvOffer.text = "$discountValue $discountType"


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

            tvExchange.text = "${productData?.returnDays} Days Return & ${productData?.exchangableDays} days Exchange"

        }
    }

    override fun initControl() {
        binding?.apply {

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
                addToBag()
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
                showToast(response.message)
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
                showToast(response.message)
                if (productData?.wishlist==1) {
                    showToast("Product removed from your wishlist successfully")
                    productData?.wishlist==0
                    binding?.ivLike?.setImageResource(R.drawable.ic_like)
                }
                else{
                    showToast("Product added to your wishlist successfully")
                    productData?.wishlist==1
                    binding?.ivLike?.setImageResource(R.drawable.ic_like_filled)
                }
                isObserver = false
            }
        }

    }

    private fun checkCartData(quantity: Int) {
        binding?.apply {
            if (quantity > 0) {
                tvCount.text = quantity.toString()
                btnAddToBag.isVisible = false
                clCartQuantity.isVisible = true
            }
            else{
                btnAddToBag.isVisible = true
                clCartQuantity.isVisible = false
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

}
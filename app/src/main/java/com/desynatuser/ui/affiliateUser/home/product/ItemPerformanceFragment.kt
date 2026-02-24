package com.desynatuser.ui.affiliateUser.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentItemPerformanceBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductDetailResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.openCustomDatePicker
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.text.toFloatOrNull

@AndroidEntryPoint
class ItemPerformanceFragment : BaseFragment() {
    private var binding: FragmentItemPerformanceBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var productData :GetAffiliateMyProductResponse.Data.Data?=null
    private var productDetailData : GetAffiliateMyProductDetailResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemPerformanceBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemProductDataJson = arguments?.getString("productData")
        if (!itemProductDataJson.isNullOrEmpty()) {
            productData = Gson().fromJson(itemProductDataJson, GetAffiliateMyProductResponse.Data.Data::class.java)
            getAffiliateMyProductDetail()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Item Performance"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivCopy.setOnClickListener {
                CommonMethod.copyText(requireActivity(),"${productDetailData?.affiliateProductLink?.link?:""}")
            }

            etFrom.setOnClickListener{
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    etFrom.setText(date)
                }
            }

            etTo.setOnClickListener{
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    etFrom.setText(date)
                }
            }
        }
    }

    private fun getAffiliateMyProductDetail(){
        val map = HashMap<String, Any>()
        map["id"] = productData?.productId.toString()
        viewModel.getAffiliateMyProductDetail(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getAffiliateMyProductDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    productDetailData = response.data
                    setData()
                }
                isObserver = false
            }
        }
    }

    private fun setData() {
        binding?.apply {
            Glide.with(requireActivity()).load(productDetailData?.product?.productVariants?.get(0)?.variantImage?.get(0)).placeholder(
                R.drawable.ic_placeholder).into(ivStore)
            if (productDetailData?.product?.store?.storeName!=null){
                tvTitle.text = productData?.product?.productNameEn+" (By ${productDetailData?.product?.store?.storeName})"
            }
            else{
                tvTitle.text = productData?.product?.productNameEn
            }
            tvRating.text ="(${productData?.averageRating?.product?:0})"
            rating.rating = productData?.averageRating?.product?.toFloatOrNull()?:0f
            tvPrice.text = "${CommonMethod.formatPrice(productDetailData?.product?.productVariants?.get(0)?.actualPrice?.toDoubleOrNull()?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(productDetailData?.product?.productVariants?.get(0)?.finalPrice?.toDoubleOrNull()?:0.0)} KD"

            val variant = productDetailData?.product?.productVariants?.getOrNull(0)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"
            tvCommission.text = "${productDetailData?.product?.affiliatedCommission}% Commission"
            if (productDetailData?.affiliateProductLink!=null){
                tvLink.isVisible = true
                ivCopy.isVisible = true
                tvLink.text = "Link- off ${productDetailData?.affiliateProductLink?.link}"
            }
            else{
                tvLink.isVisible = false
                ivCopy.isVisible = false
            }
        }
    }
}
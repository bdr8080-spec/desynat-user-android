package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.MyBagAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentMyBagBinding
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.ui.home.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.response.GetAddressResponse
import com.desynatuser.ui.home.response.GetPromoCodeResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBagFragment : BaseFragment() {
    private var binding: FragmentMyBagBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isCartObserver = false
    var isAddressObserver = false
    private var myBagAdapter: MyBagAdapter?=null
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var cartData : GetCartResponse.Data?=null
    private var selectedAddress : GetAddressResponse.Data?=null
    private var promoCode : ApplyPromoCodeResponse.Data?=null
    var selectedPos = -1
    var actionType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBagBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        binding?.apply {

            parentFragmentManager.setFragmentResultListener("address_request", viewLifecycleOwner) { _, bundle ->

                val addressJson = bundle.getString("address")
                selectedAddress = Gson().fromJson(addressJson, GetAddressResponse.Data::class.java)
                tvAddress.text = selectedAddress?.address
            }

            parentFragmentManager.setFragmentResultListener(
                "promocode_request",
                viewLifecycleOwner
            ) { _, bundle ->
                val promoCodeJson = bundle.getString("promoCode")
                promoCode = Gson().fromJson(promoCodeJson, ApplyPromoCodeResponse.Data::class.java)
                tvApplyPromoCode.isVisible = false
                tvPromoCode.isVisible = true
                tvRemove.isVisible = true
                tvCouponDiscount.isVisible = true
                tvCouponDiscountValue.isVisible = true
                tvPromoCode.text = promoCode?.discountCode
//                tvDiscountedPrice.text = "${CommonMethod.formatPrice(promoCode?.finalAmount?:0.0)} KD"
//                tvTotalAmountValue.text = "${CommonMethod.formatPrice(promoCode?.finalAmount?:0.0)} KD"
//                tvCouponDiscountValue.text = "${CommonMethod.formatPrice(promoCode?.applyDiscountValue?:0.0)} KD"
                tvCouponDiscount.text = "Coupon Discount (${promoCode?.discountCode})"

                promoCode?.let { promo ->
                    updateBillingDetails(
                        totalMrp = cartData?.totalMrp ?: 0.0,
                        discountOnMrp = cartData?.discountOnMrp ?: 0.0,
                        shippingFees = cartData?.shippingFees ?: 0.0,
                        tax = cartData?.tax ?: 0.0,
                        couponDiscount = promo.applyDiscountValue ?: 0.0,
                        finalAmount = promo.finalAmount ?: 0.0
                    )
                }

                Log.d("PromoCode", "Received: $promoCode")
            }

            myBagAdapter = MyBagAdapter(requireActivity(), myBagList,object :
                MyBagAdapter.FoodBagClick{
                override fun onFoodBagClick(position: Int,type:String) {
                    selectedPos = position
                    addCartQuantity(type)
                }

            })

            rvMyBag.adapter = myBagAdapter
        }
        if (cartData==null) {
            viewCartDetail()
        }
        else{
            checkData()
        }
    }

    override fun initControl() {
        binding?.apply {

            btnPlaceOrder.setOnClickListener {
                if(selectedAddress==null){
                    showToast("Please select address")
                }
                else{
                    val bundle = Bundle().apply {
                        putString("from", "myBag")
                        putString("cart", Gson().toJson(cartData))
                        putString("address", Gson().toJson(selectedAddress))
                        putString("promo", Gson().toJson(promoCode))
                    }
                    findNavController().navigate(R.id.action_myBagFragment_to_orderPaymentFragment,bundle)
                }
            }

            relDeliveryAddress.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "myBag")
                }
                findNavController().navigate(R.id.action_myBagFragment_to_savedAddressFragment,bundle)
            }

            relPromoCode.setOnClickListener {
                if(promoCode==null) {
                    if (selectedAddress == null) {
                        showToast("Please select delivery address first")
                    } else {
                        val bundle = Bundle().apply {
                            putString("deliveryid", selectedAddress?.id.toString())
                            putString("amount", cartData?.totalAmount.toString())
                        }
                        findNavController().navigate(
                            R.id.action_myBagFragment_to_promoCodeFragment,
                            bundle
                        )
                    }
                }
            }

            tvRemove.setOnClickListener {
                tvApplyPromoCode.isVisible = true
                tvPromoCode.isVisible = false
                tvRemove.isVisible = false
                tvCouponDiscount.isVisible = false
                tvCouponDiscountValue.isVisible = false
                promoCode = null
                tvPromoCode.text = ""
                tvCouponDiscountValue.text = "0 KD"
                setData()
            }

        }
    }

    private fun addCartQuantity(type:String){
        actionType = type
        val map = HashMap<String,Any>()
        map["cart_id"] = myBagList[selectedPos].id.toString()
        map["type"] = type//// increment,decrement
        viewModel.addCartQuantity(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun viewCartDetail(){
        viewModel.viewCartDetail()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCartObserver = true
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
                cartData = response.data
               // setData()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                myBagAdapter?.notifyDataSetChanged()
                Log.e("amount", "promoCode: ${promoCode}" )
                if (promoCode != null) {
                    Log.e("amount", "promoCode apply: ${cartData}" )

                    applyPromoOnUpdatedCart()
                } else {
                    Log.e("amount", "setData apply: ${cartData}" )

                    setData()
                }


                Constants.cartItem = myBagList.size
                (requireActivity() as HomeActivity).updateAdapterCount()
                checkData()
                isCartObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                viewCartDetail()
               /* ProgressDialogUtils.getInstance().hideProgress()
                if (myBagList[selectedPos].quantity==1){
                    if (actionType=="increment"){
                        myBagList[selectedPos].quantity = response.data.quantity
                        myBagAdapter?.notifyItemChanged(selectedPos)
                    }
                    else {
                        myBagList.removeAt(selectedPos)
                        myBagAdapter?.notifyItemRemoved(selectedPos)
                        if (myBagList.size != 0) {
                            myBagAdapter?.notifyItemRangeChanged(
                                selectedPos,
                                myBagList.size - selectedPos
                            )
                        }
                    }
                }
                else{
                    if (response.data.quantity!=0){
                        myBagList[selectedPos].quantity = response.data.quantity
                        myBagAdapter?.notifyItemChanged(selectedPos)
                    }
                    else{
                        myBagList.removeAt(selectedPos)
                        myBagAdapter?.notifyItemRemoved(selectedPos)

                        if (myBagList.size!=0) {
                            myBagAdapter?.notifyItemRangeChanged(
                                selectedPos,
                                myBagList.size - selectedPos
                            )
                        }
                    }
                }
                Constants.cartItem = myBagList.size
                (requireActivity() as HomeActivity).updateAdapterCount()
                checkData()
                selectedPos = -1*/
                isObserver = false
            }
        }

        viewModel.addressResult.observe(viewLifecycleOwner) { response ->
            if (isAddressObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                val savedAddressList = ArrayList<GetAddressResponse.Data>()
                savedAddressList.clear()
                 savedAddressList.addAll(response.data)
                if (savedAddressList.isNotEmpty()){
                    val defaultIndex = savedAddressList.indexOfFirst { it.setAsDefault == "1" }

                    if (defaultIndex != -1) {
                        selectedAddress = savedAddressList[defaultIndex]
                        binding?.tvAddress?.text = selectedAddress?.address}
                        SharedPreference.get(requireActivity()).saveDefaultAddress(savedAddressList[defaultIndex])

                    }
                isAddressObserver = false
                }
            }

    }

    private fun setData() {
        binding?.apply {
//            tvTotalMRP.text = "${CommonMethod.formatPrice(cartData?.totalMrp?:0.0)} KD"
//            tvDiscountOnMRP.text = "${CommonMethod.formatPrice(cartData?.discountOnMrp?:0.0)} KD"
//            tvCouponDiscountValue.text = "${CommonMethod.formatPrice(cartData?.discountCoupon?:0.0)} KD"
//            tvTotalAmountValue.text = "${CommonMethod.formatPrice(cartData?.totalAmount?:0.0)} KD"
//            tvShippingFee.text = "${CommonMethod.formatPrice(cartData?.shippingFees?:0.0)} KD"
//            tvTaxValue.text = "${CommonMethod.formatPrice(cartData?.tax?:0.0)} KD"
//
//            tvDiscountedPrice.text = "${CommonMethod.formatPrice(cartData?.totalAmount?:0.0)} KD"
//            tvPrice.text = "${CommonMethod.formatPrice(cartData?.totalMrp?:0.0)} KD"
//            tvSavePrice.text = "You will save ${CommonMethod.formatPrice(cartData?.totalMrp?:0.0)} KD on this"
//

            cartData?.let {
                updateBillingDetails(
                    totalMrp = it.totalMrp,
                    discountOnMrp = it.discountOnMrp,
                    shippingFees = it.shippingFees,
                    tax = it.tax,
                    couponDiscount = it.discountCoupon,
                    finalAmount = it.totalAmount
                )
            }
        }
    }

    private fun checkData() {
        if (myBagList.isEmpty()){
            binding?.tvNoDataFound?.isVisible = true
            binding?.nsBag?.isVisible = false
            binding?.clPlaceOrder?.isVisible = false
        }
        else{
            binding?.tvNoDataFound?.isVisible = false
            binding?.nsBag?.isVisible = true
            binding?.clPlaceOrder?.isVisible = true
            if(binding?.tvAddress?.text?.isEmpty() == true) {
                if (SharedPreference.get(requireActivity()).getDefaultAddress()!=null){
                selectedAddress = SharedPreference.get(requireActivity()).getDefaultAddress()
                binding?.tvAddress?.text = selectedAddress?.address}
                else{
                    getAddress()
                }
            }
        }

    }

    private fun getAddress() {
        viewModel.getAddress()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isAddressObserver = true
    }

    private fun updateBillingDetails(
        totalMrp: Double,
        discountOnMrp: Double,
        shippingFees: Double,
        tax: Double,
        couponDiscount: Double = 0.0,
        finalAmount: Double
    ) {

        Log.e("amount", "updateBillingDetails: ${finalAmount}" )
        binding?.apply {

            // Main values
            tvTotalMRP.text = "${CommonMethod.formatPrice(totalMrp)} KD"
            tvDiscountOnMRP.text = "${CommonMethod.formatPrice(discountOnMrp)} KD"
            tvShippingFee.text = "${CommonMethod.formatPrice(shippingFees)} KD"
            tvTaxValue.text = "${CommonMethod.formatPrice(tax)} KD"

            // Coupon discount (if any)
            tvCouponDiscountValue.text = "${CommonMethod.formatPrice(couponDiscount)} KD"

            // Final payable amount
            tvTotalAmountValue.text = "${CommonMethod.formatPrice(finalAmount)} KD"
            tvDiscountedPrice.text = "${CommonMethod.formatPrice(finalAmount)} KD"

            // Show summary text
            tvPrice.text = "${CommonMethod.formatPrice(totalMrp)} KD"
            tvSavePrice.text = "You will save ${
                CommonMethod.formatPrice(discountOnMrp + couponDiscount)
            } KD on this"
        }
    }

    private fun applyPromoOnUpdatedCart() {
        promoCode?.let { promo ->
            val amo =  (cartData?.totalAmount ?: 0.0) - (promo.applyDiscountValue ?: 0.0)
            Log.e("amount", "totalAmount: calculate ${amo}" )
            updateBillingDetails(
                totalMrp = cartData?.totalMrp ?: 0.0,
                discountOnMrp = cartData?.discountOnMrp ?: 0.0,
                shippingFees = cartData?.shippingFees ?: 0.0,
                tax = cartData?.tax ?: 0.0,
                couponDiscount = promo.applyDiscountValue ?: 0.0,
                finalAmount = (cartData?.totalAmount ?: 0.0) - (promo.applyDiscountValue ?: 0.0)
            )

            Log.e("amount", "totalAmount: applyPromoOnUpdatedCart ${cartData?.totalAmount}" )
            Log.e("amount", "applyDiscountValue: applyPromoOnUpdatedCart ${promo.applyDiscountValue}" )


            binding?.apply {
                tvApplyPromoCode.isVisible = false
                tvPromoCode.isVisible = true
                tvRemove.isVisible = true
                tvCouponDiscount.isVisible = true
                tvCouponDiscountValue.isVisible = true
                tvPromoCode.text = promo.discountCode
                tvCouponDiscount.text = "Coupon Discount (${promo.discountCode})"
            }
        }
    }



}
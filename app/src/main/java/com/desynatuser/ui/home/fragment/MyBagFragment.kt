package com.desynatuser.ui.home.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.MyBagAdapter
import com.desynatuser.adapter.MyCustomBagAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.CustomDetailDialogBinding
import com.desynatuser.databinding.FragmentMyBagBinding
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.ui.home.profile.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.profile.response.GetAddressResponse
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
    private var binding: FragmentMyBagBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isCartObserver = false
    var isAddressObserver = false
    private var myBagAdapter: MyBagAdapter? = null
    private var myCustomBagAdapter: MyCustomBagAdapter? = null
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var myCartList = ArrayList<GetCartResponse.Data.CartData>()
    private var myCustomBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var cartData: GetCartResponse.Data? = null
    private var selectedAddress: GetAddressResponse.Data? = null
    private var promoCode: ApplyPromoCodeResponse.Data? = null
    var selectedPos = -1
    var actionType = ""
    var customPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBagBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun onResume() {
        super.onResume()
        viewCartDetail()
    }

    override fun initView() {

        binding?.apply {

            parentFragmentManager.setFragmentResultListener(
                "address_request",
                viewLifecycleOwner
            ) { _, bundle ->

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
                tvPromoCode.text = if (Constants.language=="ar") promoCode?.discountCodeAr else promoCode?.discountCode
//                tvDiscountedPrice.text = "${CommonMethod.formatPrice(promoCode?.finalAmount?:0.0)} KD"
//                tvTotalAmountValue.text = "${CommonMethod.formatPrice(promoCode?.finalAmount?:0.0)} KD"
//                tvCouponDiscountValue.text = "${CommonMethod.formatPrice(promoCode?.applyDiscountValue?:0.0)} KD"
                tvCouponDiscount.text = "${getString(R.string.coupon_discount)} (${promoCode?.discountCode})"

                promoCode?.let { promo ->
                    updateBillingDetails(
                        totalMrp = cartData?.totalMrp ?: 0.0,
                        discountOnMrp = cartData?.discountOnMrp ?: 0.0,
                        shippingFees = cartData?.shippingFees ?: 0.0,
                        tax = cartData?.tax ?: 0.0,
                        couponDiscount = promo.applyDiscountValue ?: 0.0,
                        totalAmount = promo.finalAmount ?: 0.0
                    )
                }

                Log.d("PromoCode", "Received: $promoCode")
            }

            myBagAdapter = MyBagAdapter(requireActivity(), myBagList, object :
                MyBagAdapter.FoodBagClick {
                override fun onFoodBagClick(position: Int, type: String) {
                    when(type){
                        "custom"->{
                            customDetailDialog(myBagList[position]?.chatroomData?.customDetails?:"")
                        }
                        "remove"->{
                            removeFromCartCustomOrder(myBagList[position].id)
                        }
                        else ->{
                            selectedPos = position
                            addCartQuantity(type)
                        }
                    }
                }
            })

            rvMyBag.adapter = myBagAdapter

            myCustomBagAdapter = MyCustomBagAdapter(requireActivity(), myCustomBagList, object :
                MyCustomBagAdapter.FoodBagClick {
                override fun onFoodBagClick(position: Int, type: String) {
                    when(type){
                        "custom"->{
                            customDetailDialog(myCustomBagList[position]?.chatroomData?.customDetails?:"")
                        }
                        "remove"->{
                            removeFromCartCustomOrder(myCustomBagList[position].id)
                        }
                        else ->{
                            selectedPos = position
                            addCartQuantity(type)
                        }
                    }
                }
            })

            rvCustomMyBag.adapter = myCustomBagAdapter
        }

//        if (cartData==null) {
//            viewCartDetail()
//        }
//        else{
//            checkData()
//        }
    }

    override fun initControl() {
        binding?.apply {

            btnPlaceOrder.setOnClickListener {
                if (selectedAddress == null) {
                    showToast(getString(R.string.please_select_address))
                } else {
                    val bundle = Bundle().apply {
                        putString("from", "myBag")
                        putString("cart", Gson().toJson(cartData))
                        putString("address", Gson().toJson(selectedAddress))
                        putString("promo", Gson().toJson(promoCode))
                        putDouble("customPrice", customPrice)
                    }
                    findNavController().navigate(
                        R.id.action_myBagFragment_to_orderPaymentFragment,
                        bundle
                    )
                }
            }

            relDeliveryAddress.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "myBag")
                }
                findNavController().navigate(
                    R.id.action_myBagFragment_to_savedAddressFragment,
                    bundle
                )
            }

            relPromoCode.setOnClickListener {
                if (promoCode == null) {
                    if (selectedAddress == null) {
                        showToast(getString(R.string.please_select_delivery_address_first))
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
                tvCouponDiscountValue.text = "0 ${Constants.CURRENCY}"
                setData()
            }

        }
    }

    private fun addCartQuantity(type: String) {
        actionType = type
        val map = HashMap<String, Any>()
        map["cart_id"] = myBagList[selectedPos].id.toString()
        map["type"] = type//// increment,decrement
        viewModel.addCartQuantity(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun removeFromCartCustomOrder(id: String) {
        val map = HashMap<String, Any>()
        map["id"] = id
        viewModel.removeFromCartCustomOrder(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun viewCartDetail() {
        viewModel.viewCartDetail()
        binding?.progressBar?.isVisible = true
        isCartObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.cartDetailLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                cartData = response.data
                myBagList.clear()
                myCartList.clear()
                myCustomBagList.clear()
                response?.data?.cartData?.let { myCartList.addAll(it) }
                for (i in 0 until myCartList.size){
                    if (myCartList[i]?.orderType=="2"){
                        myCartList?.let { myCustomBagList.add(myCartList[i]) }
                    }
                    else{
                        myCartList?.let { myBagList.add(myCartList[i]) }
                    }
                }

                myBagAdapter?.notifyDataSetChanged()
                myCustomBagAdapter?.notifyDataSetChanged()
                Log.e("amount", "promoCode: ${promoCode}")
                if (promoCode != null) {
                    Log.e("amount", "promoCode apply: ${cartData}")
                    applyPromoOnUpdatedCart()
                } else {
                    Log.e("amount", "setData apply: ${cartData}")

                    setData()
                }

                (requireActivity() as HomeActivity).updateAdapterCount(myCartList.size)
                checkData()
                isCartObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                viewCartDetail()
                isObserver = false
            }
        }

        viewModel.removeFromCartCustomOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                viewCartDetail()
                isObserver = false
            }
        }

        viewModel.addressResult.observe(viewLifecycleOwner) { response ->
            if (isAddressObserver) {
                binding?.progressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                val savedAddressList = ArrayList<GetAddressResponse.Data>()
                savedAddressList.clear()
                savedAddressList.addAll(response.data)
                if (savedAddressList.isNotEmpty()) {
                    val defaultIndex = savedAddressList.indexOfFirst { it.setAsDefault == "1" }

                    if (defaultIndex != -1) {
                        selectedAddress = savedAddressList[defaultIndex]
                        binding?.tvAddress?.text = selectedAddress?.address
                    }
                    SharedPreference.get(requireActivity())
                        .saveDefaultAddress(savedAddressList[defaultIndex])

                }
                isAddressObserver = false
            }
        }

    }

    private fun setData() {
        binding?.apply {
            cartData?.let {
                updateBillingDetails(
                    totalMrp = it.totalMrp,
                    discountOnMrp = it.discountOnMrp,
                    shippingFees = it.shippingFees,
                    tax = it.tax,
                    couponDiscount = it.discountCoupon,
                    totalAmount = it.totalAmount
                )
            }
        }
    }

    private fun checkData() {
        binding?.apply {

            if (myCartList.isEmpty()) {
                tvNoDataFound.isVisible = true
               nsBag.isVisible = false
                clPlaceOrder.isVisible = false
            } else {
                tvNoDataFound.isVisible = false
                nsBag.isVisible = true
                clPlaceOrder.isVisible = true
                if (tvAddress.text.isEmpty() == true) {
                    if (SharedPreference.get(requireActivity()).getDefaultAddress() != null) {
                        selectedAddress = SharedPreference.get(requireActivity()).getDefaultAddress()
                        tvAddress.text = selectedAddress?.address
                    } else {
                        getAddress()
                    }
                }
            }

            if (myCustomBagList.size!=0){
               relCustomCart.isVisible = true
                tvCustomOrder.isVisible = true
            }
            else{
                relCustomCart.isVisible = false
                tvCustomOrder.isVisible = false
            }

            if (myBagList.size!=0){
                rvMyBag.isVisible = true
            }
            else{
                rvMyBag.isVisible = false
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
        totalAmount: Double
    ) {

        Log.e("amount", "updateBillingDetails: ${totalAmount}")
        binding?.apply {
            customPrice = calculateCustomOrderTotal()
            val finalAmount = totalAmount+customPrice
            Log.e("amount", "customPrice: ${customPrice}")

            if (customPrice!=0.0){
                tvCustomPrice.isVisible = true
                tvCustomPriceValue.isVisible = true
                tvCustomPriceValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),customPrice)}"
            }
            else{
                tvCustomPrice.isVisible = false
                tvCustomPriceValue.isVisible = false
            }


            if (CommonMethod.formatPrice(totalMrp)!="0"){
                tvTotalMRP.isVisible = true
                tvMrp.isVisible = true
              //  tvTotalMRP.text = "${CommonMethod.formatPrice(totalMrp)} KD"
                tvTotalMRP.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),totalMrp)}"
            }
            else{
                tvTotalMRP.isVisible = false
                tvMrp.isVisible = false
                if (myCartList.size==1){
                //    tvCustomPrice.text = "Item Price (${CommonMethod.formatPrice(myCartList[0]?.chatroomData?.price?.toDoubleOrNull()?:0.0)} KD X ${myCartList[0]?.chatroomData?.quantity})"
                    tvCustomPrice.text = "${getString(R.string.item_price)} (${CommonMethod.formatCurrencyAmount(requireActivity(),myCartList[0]?.chatroomData?.price?.toDoubleOrNull()?:0.0)} X ${myCartList[0]?.chatroomData?.quantity})"
                }
                else{
                    tvCustomPrice.text = "${getString(R.string.custom_item_price)}"
                }
            }

            if (CommonMethod.formatPrice(discountOnMrp)!="0"){
                tvDiscountOnMrp.isVisible = true
                tvDiscountOnMRP.isVisible = true
             //   tvDiscountOnMRP.text = "${CommonMethod.formatPrice(discountOnMrp)} KD"
                tvDiscountOnMRP.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),discountOnMrp)}"

            }
            else{
                tvDiscountOnMrp.isVisible = false
                tvDiscountOnMRP.isVisible = false
            }

            if (CommonMethod.formatPrice(shippingFees)=="0"){
                tvShippingFee.text = getString(R.string.free_delivery)
            }
            else{
               // tvShippingFee.text = "${CommonMethod.formatPrice(shippingFees)} KD"
                tvShippingFee.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),shippingFees)}"
            }
        //    tvTaxValue.text = "${CommonMethod.formatPrice(tax)} KD"
            tvTaxValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),tax)}"

            // Coupon discount (if any)
      //      tvCouponDiscountValue.text = "${CommonMethod.formatPrice(couponDiscount)} KD"
            tvCouponDiscountValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),couponDiscount)}"

            // Final payable amount
//            tvTotalAmountValue.text = "${CommonMethod.formatPrice(finalAmount)} KD"
//            tvDiscountedPrice.text = "${CommonMethod.formatPrice(finalAmount)} KD"

            tvTotalAmountValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),finalAmount)}"
            tvDiscountedPrice.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),finalAmount)}"

            // Show summary text
          //  tvPrice.text = "${CommonMethod.formatPrice(finalAmount+discountOnMrp+couponDiscount)} KD"
            tvPrice.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),finalAmount+discountOnMrp+couponDiscount)}"
            if (CommonMethod.formatPrice(discountOnMrp + couponDiscount)!="0"){
//                tvSavePrice.text = "You will save ${
//                    CommonMethod.formatPrice(discountOnMrp + couponDiscount)
//                } KD on this"
                tvSavePrice.text = "${getString(R.string.you_will_save)} ${
                    CommonMethod.formatCurrencyAmount(requireActivity(),discountOnMrp + couponDiscount)
                } ${getString(R.string.on_this)}"
                tvSavePrice.isVisible = true
            }
            else{
                tvSavePrice.isInvisible = true
            }

        }
    }

    private fun applyPromoOnUpdatedCart() {
        promoCode?.let { promo ->

            val baseAmount = cartData?.totalAmount ?: 0.0

            val finalAmount = baseAmount - (promo.applyDiscountValue ?: 0.0)

            updateBillingDetails(
                totalMrp = cartData?.totalMrp ?: 0.0,
                discountOnMrp = cartData?.discountOnMrp ?: 0.0,
                shippingFees = cartData?.shippingFees ?: 0.0,
                tax = cartData?.tax ?: 0.0,
                couponDiscount = promo.applyDiscountValue ?: 0.0,
                totalAmount = finalAmount
            )

            Log.e("amount", "totalAmount: applyPromoOnUpdatedCart ${cartData?.totalAmount}")
            Log.e(
                "amount",
                "applyDiscountValue: applyPromoOnUpdatedCart ${promo.applyDiscountValue}"
            )


            binding?.apply {
                tvApplyPromoCode.isVisible = false
                tvPromoCode.isVisible = true
                tvRemove.isVisible = true
                tvCouponDiscount.isVisible = true
                tvCouponDiscountValue.isVisible = true
                tvPromoCode.text = if (Constants.language=="ar") promoCode?.discountCodeAr else promo.discountCode
                tvCouponDiscount.text = "${getString(R.string.coupon_discount)} (${promo.discountCode})"
            }
        }
    }

    private fun customDetailDialog(customDetail: String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = CustomDetailDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding?.apply {

            tvCustomDetail.text = customDetail

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

        }
        dialog?.show()
    }

    private fun calculateCustomOrderTotal(): Double {
        var customTotal = 0.0

        myCartList.forEach { item ->
            if (item.orderType == "2") {
                val chatData = item.chatroomData
                customTotal += chatData?.totalAmount?.toDoubleOrNull() ?: 0.0
            }
        }
        return customTotal
    }


}
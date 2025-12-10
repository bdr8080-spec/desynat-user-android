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
import com.desynatuser.ui.home.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.response.GetAddressResponse
import com.desynatuser.ui.home.response.GetPromoCodeResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.CommonMethod
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
    private var myBagAdapter: MyBagAdapter?=null
    private var myBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var cartData : GetCartResponse.Data?=null
    private var selectedAddress : GetAddressResponse.Data?=null
    private var promoCode : ApplyPromoCodeResponse.Data?=null
    var selectedPos = -1

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
                tvPromoCode.text = promoCode?.discountCode
                tvTotalAmount.text = CommonMethod.formatPrice(promoCode?.finalAmount?:0.0)
                tvCouponDiscountValue.text = CommonMethod.formatPrice(promoCode?.originalDiscount?:0.0)
                Log.d("PromoCode", "Received: $promoCode")
            }

            myBagAdapter = MyBagAdapter(requireActivity(), myBagList,object :
                MyBagAdapter.FoodBagClick{
                override fun onFoodBagClick(position: Int,type:String) {
                    selectedPos = position
                    addCartQuantity(type)
                }

            })

            rvFoodBag.adapter = myBagAdapter
        }

        viewCartDetail()
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
                promoCode = null
                tvPromoCode.text = ""
                tvTotalAmount.text = "${cartData?.totalAmount} KD"
                tvCouponDiscountValue.text = "0 KD"
            }

        }
    }

    private fun addCartQuantity(type:String){
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
        isObserver = true
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
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cartData = response.data
                setData()
                myBagList.clear()
                response?.data?.cartData?.let { myBagList.addAll(it) }
                myBagAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (myBagList[selectedPos].quantity==1){
                    myBagList.removeAt(selectedPos)
                    myBagAdapter?.notifyItemRemoved(selectedPos)
                    if (myBagList.size!=0){
                        myBagAdapter?.notifyItemRangeChanged(selectedPos, myBagList.size - selectedPos)
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

                checkData()
                selectedPos = -1
                isObserver = false
            }
        }
    }

    private fun setData() {
        binding?.apply {
            tvTotalMRP.text = "${cartData?.totalMrp} KD"
            tvDiscountOnMRP.text = "${cartData?.discountOnMrp} KD"
            tvCouponDiscountValue.text = "${cartData?.discountCoupon} KD"
            tvTotalAmountValue.text = "${cartData?.totalAmount} KD"
            tvShippingFee.text = "${cartData?.shippingFees} KD"
            tvTaxValue.text = "${cartData?.tax} KD"

            tvDiscountedPrice.text = "${cartData?.totalAmount} KD"
            tvPrice.text = "${cartData?.totalMrp} KD"
        }
    }

    private fun checkData() {
        if (myBagList.isEmpty()){
            binding?.tvNoDataFound?.isVisible = true
            binding?.nsBag?.isVisible = false
        }
        else{
            binding?.tvNoDataFound?.isVisible = false
            binding?.nsBag?.isVisible = true
            if(binding?.tvAddress?.text?.isEmpty() == true) {
                selectedAddress = SharedPreference.get(requireActivity()).getDefaultAddress()
                binding?.tvAddress?.text = selectedAddress?.address

            }
        }

    }

}
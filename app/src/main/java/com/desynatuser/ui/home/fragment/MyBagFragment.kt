package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.FoodBagAdapter
import com.desynatuser.adapter.ProductColorAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentMyBagBinding
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBagFragment : BaseFragment() {
    private var binding: FragmentMyBagBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var foodBagAdapter: FoodBagAdapter?=null
    private var foodBagList = ArrayList<GetCartResponse.Data.CartData>()
    private var cartData : GetCartResponse.Data?=null
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

            foodBagAdapter = FoodBagAdapter(requireActivity(), foodBagList,object :
                FoodBagAdapter.FoodBagClick{
                override fun onFoodBagClick(position: Int,type:String) {
                    selectedPos = position
                    addCartQuantity(type)
                }

            })

            rvFoodBag.adapter = foodBagAdapter
        }

        viewCartDetail()
    }

    override fun initControl() {
        binding?.apply {

            btnPlaceOrder.setOnClickListener {

            }

            relDeliveryAddress.setOnClickListener {
                findNavController().navigate(R.id.action_myBagFragment_to_savedAddressFragment)
            }

        }
    }

    private fun addCartQuantity(type:String){
        val map = HashMap<String,Any>()
        map["cart_id"] = cartData?.cartData?.get(selectedPos)?.id.toString()
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
                foodBagList.clear()
                foodBagList.addAll(response.data.cartData)
                foodBagAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }

        viewModel.cartQuantityResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (foodBagList[selectedPos].quantity==1){
                    foodBagList.removeAt(selectedPos)
                    foodBagAdapter?.notifyItemRemoved(selectedPos)
                    if (foodBagList.size!=0){
                        foodBagAdapter?.notifyItemRangeChanged(selectedPos, foodBagList.size - selectedPos)
                    }
                }
                else{
                    if (response.data.quantity!=0){
                        foodBagList[selectedPos].quantity = response.data.quantity
                        foodBagAdapter?.notifyItemChanged(selectedPos)
                    }
                    else{
                            foodBagList.removeAt(selectedPos)
                        foodBagAdapter?.notifyItemRemoved(selectedPos)

                        if (foodBagList.size!=0) {
                            foodBagAdapter?.notifyItemRangeChanged(
                                selectedPos,
                                foodBagList.size - selectedPos
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
        if (foodBagList.isEmpty()){
            binding?.tvNoDataFound?.isVisible = true
            binding?.nsBag?.isVisible = false
        }
        else{
            binding?.tvNoDataFound?.isVisible = false
            binding?.nsBag?.isVisible = true
        }

    }

}
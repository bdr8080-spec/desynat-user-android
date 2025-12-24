package com.desynatuser.ui.home.myOrder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentOrderPlacedBinding
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderPlacedFragment : BaseFragment() {
    private var binding: FragmentOrderPlacedBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var selectedAddress: GetAddressResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderPlacedBinding.inflate(layoutInflater,container,false)
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
            val amount = arguments?.getString("amount")
            val addressJson = arguments?.getString("address")
            val payment = arguments?.getString("payment")
            if (!addressJson.isNullOrEmpty()) {
                selectedAddress = Gson().fromJson(addressJson, GetAddressResponse.Data::class.java)
            }
             tvDeliveryDate.text = "Expected Delivery Date : 3 to 5 working days"
//             tvDeliveryDate.text = "Expected Delivery Date : ${CommonMethod.getExpectedDeliveryDate(output = "dd MMM yy")}"
            tvPrice.text = "${CommonMethod.formatPrice(amount?.toDouble()?:0.0)} KD"
            if (payment=="COD") {
                tvPrice.text = "Cash on Delivery"
                tvPaid.isVisible = false
            }
            else if(payment =="WALLET"){
                tvPrice.text = "${CommonMethod.formatPrice(amount?.toDouble()?:0.0)} KD"
                tvPaid.isVisible = true
                tvPaid.text = "Amount Paid with Wallet"
            }
            else{
                tvPaid.text = "Amount Paid"
                tvPrice.text = "${CommonMethod.formatPrice(amount?.toDouble()?:0.0)} KD"
                tvPaid.isVisible = true
            }
                  tvAddress.text = selectedAddress?.name
                  tvAddress1.text = selectedAddress?.address

        /*    lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    lottieView.isVisible = false
                    ivSuccess.isVisible = true
                }
            })*/
        }
    }

    override fun initControl() {
        binding?.apply {

            tvTrackOrder.setOnClickListener {
                lottieView.cancelAnimation()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.home, false)
                    .build()

                findNavController().navigate(
                    R.id.action_orderPlacedFragment_to_myOrderFragment,
                    null,
                    navOptions
                )
           }

            btnContinueShopping.setOnClickListener {
                lottieView.cancelAnimation()
                findNavController().navigate(
                    R.id.home,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.home_navigation_graph, true)
                        .build()
                )

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

    }

}

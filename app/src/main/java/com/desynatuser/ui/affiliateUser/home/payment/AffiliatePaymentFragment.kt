package com.desynatuser.ui.affiliateUser.home.payment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliatePaymentAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliatePaymentBinding
import com.desynatuser.ui.affiliateUser.home.payment.response.GetAllAffiliateOrderResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliatePaymentFragment : BaseFragment() {
    private var binding: FragmentAffiliatePaymentBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var paymentAdapter: AffiliatePaymentAdapter? = null
    private var paymentData: GetAllAffiliateOrderResponse.Data?=null
    private var paymentList = ArrayList<GetAllAffiliateOrderResponse.Data.Order>()
    private var animator: ObjectAnimator?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliatePaymentBinding.inflate(layoutInflater, container, false)
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

            animator = ObjectAnimator.ofFloat(ivRefresh, View.ROTATION, 0f, 360f)
            animator?.duration = 800
            animator?.repeatCount = ValueAnimator.INFINITE
            animator?.interpolator = LinearInterpolator()

            paymentAdapter = AffiliatePaymentAdapter(requireActivity(), paymentList, object :
                AffiliatePaymentAdapter.OnClick {
                override fun click(position: Int, type: Int) {

                }

            })
            rvPayment.adapter = paymentAdapter
        }

        getAffiliatePayment()
    }

    override fun initControl() {
        binding?.apply {
            tvWithdraw.setOnClickListener {
                findNavController().navigate(R.id.action_affiliatePaymentFragment_to_affiliateWithdrawMoneyFragment)
            }

            ivRefresh.setOnClickListener {
                animator?.start()
                getAffiliatePayment(false)
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

        viewModel.getAffiliatePaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                animator?.cancel()
                binding?.ivRefresh?.setImageResource(R.drawable.ic_refresh)
                response?.let {
                    paymentData = response.data
                    paymentList.clear()
                    response.data?.orders?.let { c -> paymentList.addAll(c) }
                    paymentAdapter?.notifyDataSetChanged()
                    checkData()
                    setData()
                }
                isObserver = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = paymentList.isEmpty()
    }

    private fun getAffiliatePayment(isLoading: Boolean=true){
        viewModel.getAffiliatePayment()
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun setData(){
        binding?.apply {
            tvAvailable.text =  "${CommonMethod.formatPrice(paymentData?.totalEarning?.toDoubleOrNull()?:0.0)} KD"

        }
    }

}

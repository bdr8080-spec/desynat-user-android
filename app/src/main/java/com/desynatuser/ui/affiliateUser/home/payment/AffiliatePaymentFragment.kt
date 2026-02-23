package com.desynatuser.ui.affiliateUser.home.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatuser.R
import com.desynatuser.adapter.AffiliatePaymentAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliatePaymentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliatePaymentFragment : BaseFragment() {
    private var binding: FragmentAffiliatePaymentBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var paymentAdapter: AffiliatePaymentAdapter?=null
    private var paymentList = ArrayList<String>()

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
            paymentAdapter = AffiliatePaymentAdapter(requireActivity(),paymentList,object :
                AffiliatePaymentAdapter.OnClick{
                override fun click(position: Int, type: Int) {

                }

            })
            rvPayment.adapter = paymentAdapter

        }
    }

    override fun initControl() {

    }

    override fun observe() {
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = paymentList.isEmpty()
    }
}

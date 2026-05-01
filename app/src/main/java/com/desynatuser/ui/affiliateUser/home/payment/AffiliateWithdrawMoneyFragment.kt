package com.desynatuser.ui.affiliateUser.home.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.BankAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateWithdrawMoneyBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateWithdrawMoneyFragment : BaseFragment() {
    private var binding: FragmentAffiliateWithdrawMoneyBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var profileData: GetAffiliateUserProfileResponse.Data? = null
    private var bankList = ArrayList<GetAffiliateUserProfileResponse.Data.BankDetails>()
    private var bankAdapter: BankAdapter? = null
    private var selectedBank: GetAffiliateUserProfileResponse.Data.BankDetails?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateWithdrawMoneyBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.withdraw_money)
            profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
            bankList.clear()
            profileData?.bankDetails?.let { bankList.addAll(it) }
            bankAdapter = BankAdapter(
                requireActivity(),
                bankList,
                isShow = false,
                object : BankAdapter.OnClick {
                    override fun onClick(position: Int, type: Int) {
                        when (type) {
                            3 -> {
                                selectedBank = bankList[position]
                            }
                        }
                    }

                })

            rvBank.adapter = bankAdapter

            for (i in 0 until bankList.size){
                if (bankList[i].defaultAccount=="1"){
                    selectedBank = bankList[i]
                }
            }

            tvCurrency.text = Constants.CURRENCY
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                if (etAmount.text.toString().isEmpty()){
                    showToast(getString(R.string.please_enter_amount))
                }
                else if (selectedBank==null){
                    showToast(getString(R.string.please_select_bank_account))
                }
                else{
                    withdrawAffiliateAmount()
                }

            }

            clAddAnotherAccount.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("userId", profileData?.userId.toString())
                    putString("from", "profile")
                    putString("type", "add")
                }
                findNavController().navigate(
                    R.id.action_affiliateWithdrawMoneyFragment_to_affiliateAddBankDetailFragment,bundle
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

        viewModel.withdrawAffiliateAmountResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessWithTimeDialog(requireActivity(), getString(R.string.request_sent_successfully), time = 1500) {
                        findNavController().popBackStack()
                    }
                }
                isObserver = false
            }
        }
    }

    private fun withdrawAffiliateAmount() {
        val map = HashMap<String, Any>()
        map["amount"] = binding?.etAmount?.text?.trim().toString()
        map["bankdetails_id"] = selectedBank?.id.toString()
        viewModel.withdrawAffiliateAmount(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}

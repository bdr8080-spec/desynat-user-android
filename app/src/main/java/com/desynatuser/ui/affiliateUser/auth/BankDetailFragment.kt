package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentBankDetailBinding
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddBankDetailRequest
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailFragment : BaseFragment() {
    private var binding: FragmentBankDetailBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var isObserver = false
    private var isProfileObserver = false
    private var userId = ""
    private var from = ""
    private var type = ""
    private var bankData: GetAffiliateUserProfileResponse.Data.BankDetails?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBankDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        userId = arguments?.getString("userId").toString()
        from = arguments?.getString("from").toString()
        type = arguments?.getString("type").toString()

        binding?.apply {


            if (from=="profile"){
                llTab.isVisible = false
                if (type=="edit"){
                    toolbar.tvToolbar.text = "Edit Account"
                    btnSubmit.text = "Edit Account"

                    val itemBankDataJson = arguments?.getString("bankData")
                    if (!itemBankDataJson.isNullOrEmpty()) {
                        bankData = Gson().fromJson(itemBankDataJson,
                            GetAffiliateUserProfileResponse.Data.BankDetails::class.java)
                        setData()
                    }

                }
                else{
                    toolbar.tvToolbar.text = "Add Account"
                    btnSubmit.text = "Add Account"
                }
            }
            else{
                llTab.isVisible = true
                toolbar.tvToolbar.text = "Bank Details"
                btnSubmit.text = "Submit"
            }
            CommonMethod.removeSpace(etAccountNumber)
            CommonMethod.removeSpace(etAccountHolderName)
            CommonMethod.removeSpace(etConfirmAccountNumber)
            CommonMethod.removeSpace(etIban)
            CommonMethod.removeSpace(etSwift)
            CommonMethod.removeSpace(etBankName)
        }
    }

    private fun setData() {
        binding?.apply {
            etBankName.setText(bankData?.bankName)
            etSwift.setText(bankData?.swift)
            etIban.setText(bankData?.iban)
            etAccountNumber.setText(bankData?.accountNo)
            etConfirmAccountNumber.setText(bankData?.accountNo)
            etAccountHolderName.setText(bankData?.accountHolderName)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                if (validation()){
                    addAffiliateUserBandDetail()
                }
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
 homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isProfileObserver = false
            }
        }

        viewModel.addBankAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                        CommonMethod.showSuccessDialog(
                            requireActivity(),
                            "Details Submitted Successfully"
                        ) {
                            findNavController().navigate(
                                R.id.action_affiliateAddBankDetailFragment_to_underReviewFragment
                            )
                        }
                }
                isObserver = false
            }
        }

        viewModel.updateAffiliateUserBankResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                response?.let {
                   getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.addAffiliateUserBankResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                response?.let {
                   getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        homeViewModel.getAffiliateProfileResult.observe(viewLifecycleOwner) { response ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                    when(type){
                        "edit"->{
                            CommonMethod.showSuccessDialog(
                                requireActivity(),
                                "Bank Details Updated Successfully"
                            ) {
                                findNavController().popBackStack()
                            }
                        }
                        "add"->{
                            CommonMethod.showSuccessDialog(
                                requireActivity(),
                                "Bank Details Added Successfully"
                            ) {
                                findNavController().popBackStack()
                            }
                        }

                    }

                }
                isProfileObserver = false
            }
        }

    }

    private fun getAffiliateUserProfile(){
        homeViewModel.getAffiliateProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isProfileObserver = true
    }

    private fun validation(): Boolean {
        binding?.apply {
            return when {
                etBankName.text.toString().isEmpty() -> {
                    showToast("Please enter bank name")
                    false
                }
                etAccountHolderName.text.toString().isEmpty() -> {
                    showToast("Please enter account holder name")
                    false
                }
                etAccountNumber.text.toString().isEmpty() -> {
                    showToast("Please enter account number")
                    false
                }
                etConfirmAccountNumber.text.toString().isEmpty() -> {
                    showToast("Please enter re-enter account number")
                    false
                }
                etAccountNumber.text.toString() != etConfirmAccountNumber.text.toString() -> {
                    showToast("Account number and confirm account number didn't match")
                    false
                }
                etIban.text.toString().isEmpty() -> {
                    showToast("Please enter IBAN number")
                    false
                }
                etSwift.text.toString().isEmpty() -> {
                    showToast("Please enter SWIFT/BIC number")
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun addAffiliateUserBandDetail(){
        binding?.apply {
            val request = AffiliateUserAddBankDetailRequest(
                accountHolderName = etAccountHolderName.text.toString().trim(),
                accountNo = etAccountNumber.text.toString().trim(),
                bankName = etBankName.text.toString(),
                confirmAccountNo = etConfirmAccountNumber.text.toString().trim(),
                iban = etIban.text.toString().trim(),
                swift = etSwift.text.toString().trim(),
                bankDetailsId = if (type=="edit") bankData?.id.toString() else null
            )
            if (from=="profile"){
                if (type=="edit"){
                    viewModel.updateAffiliateUserBank(request)
                }
                else{
                    viewModel.addAffiliateUserBank(request)
                }
            }
            else{
                viewModel.addAffiliateUserBankDetail(request)
            }
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

}
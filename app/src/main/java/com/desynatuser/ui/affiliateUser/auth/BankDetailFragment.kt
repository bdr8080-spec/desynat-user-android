package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentBankDetailBinding
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddBankDetailRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailFragment : BaseFragment() {
    private var binding: FragmentBankDetailBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private var isObserver = false
    private var userId = ""
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

        binding?.apply {
            toolbar.tvToolbar.text = "Bank Details"

            CommonMethod.removeSpace(etAccountNumber)
            CommonMethod.removeSpace(etAccountHolderName)
            CommonMethod.removeSpace(etConfirmAccountNumber)
            CommonMethod.removeSpace(etIban)
            CommonMethod.removeSpace(etSwift)
            CommonMethod.removeSpace(etBankName)
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
                    showToast("Please enter confirm account number")
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
                swift = etSwift.text.toString().trim()
            )
            viewModel.addAffiliateUserBandDetail(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

}
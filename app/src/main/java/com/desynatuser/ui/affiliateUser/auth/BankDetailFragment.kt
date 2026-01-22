package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentBankDetailBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailFragment : BaseFragment() {
    private var binding: FragmentBankDetailBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

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

                }
            }
        }
    }

    override fun observe() {
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

}
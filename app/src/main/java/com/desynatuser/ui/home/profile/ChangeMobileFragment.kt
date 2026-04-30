package com.desynatuser.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeMobileBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeMobileFragment : BaseFragment() {
    private var binding: FragmentChangeMobileBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var user: LoginResponse.Data?=null
    var from = "mobile"
    var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeMobileBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        setCountry()
        from = arguments?.getString("from").toString()
            binding?.apply {
                CommonMethod.removeAllTextSpaces(etEmail)
                if (from=="email"){
                    toolbar.tvToolbar.text = getString(R.string.change_email_address)
                    tvInputTitle.text = getString(R.string.enter_new_email_address)
                    tvInfo.text =
                        getString(R.string.great_now_enter_the_email_address_that_you_want_to_continue_with)
                    clMobile.isVisible = false
                    etEmail.isVisible = true
                }
                else{
                    toolbar.tvToolbar.text = getString(R.string.change_mobile_number)
                    tvInputTitle.text = getString(R.string.enter_new_mobile_number)
                    tvInfo.text =
                        getString(R.string.great_now_enter_the_mobile_number_that_you_want_to_continue_with)
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                }
            }
    }

    override fun initControl() {

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack(R.id.profileDetails, false)
            }

            btnContinue.setOnClickListener{
                if (validation()) {
                    changeMobileOrEmail()
                }
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showUserCountryPicker(requireContext(),
                    clMobile,
                    countryList,true
                ) { selectedCountry ->
                    tvCountryPicker?.text = selectedCountry.dialCode
                }            }

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

        viewModel.changeNumberResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    if (from=="email") {
                        showToast(getString(R.string.otp_sent_successfully_to_new_email_address))
                    }
                    else{
                        showToast(getString(R.string.otp_sent_successfully))
                    }
                    val bundle = Bundle().apply {
                        if (from=="email"){
                            putString("email", binding?.etEmail?.text.toString())
                        } else {
                            putString("mobileNumber", binding?.etMobile?.text.toString())
                            putString("countryCode", binding?.tvCountryPicker?.text.toString())
                        }
                        putString("from", "change")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_changeMobileEmailFragment_to_otpFragment,bundle)


                }
                isObserver = false
            }
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()

            return when {
                mobile.isEmpty()&&from=="mobile" -> {
                    showToast(getString(R.string.please_enter_new_mobile_number))
                    false
                }

                mobile.length < 7 &&from=="mobile"-> {
                    showToast(getString(R.string.please_enter_a_valid_new_mobile_number))
                    false
                }

                email.isEmpty() &&from=="email"-> {
                    showToast(getString(R.string.please_enter_new_email_address))
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&from=="email"-> {
                    showToast(getString(R.string.please_enter_a_valid_new_email_address))
                    false
                }

                else -> true
            }
        }
        return false
    }


    private fun changeMobileOrEmail(){
        val map = HashMap<String,Any>()
        map["for"] = from //mobile,email
        if (from=="email"){
            map["email"] = binding?.etEmail?.text?.trim().toString()
        }
        else{
            map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
            map["mobile"] = binding?.etMobile?.text?.trim().toString()
        }

        map["type"] = 1 //0-old,1-new
        viewModel.changeNumber(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

}
package com.desynatuser.ui.affiliateUser.home.profile

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeMobileBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileRequest
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateChangeMobileFragment : BaseFragment() {
    private var binding: FragmentChangeMobileBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isProfileObserver = false
    var from = "mobile"
    var countryList = ArrayList<GetCountryResponse.Data>()
    private var profileData : GetAffiliateUserProfileResponse.Data?=null

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
        profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
        setCountry()

        from = arguments?.getString("from").toString()
            binding?.apply {
                CommonMethod.removeAllTextSpaces(etEmail)
                if (from=="email"){
                    toolbar.tvToolbar.text = "Change Email Address"
                    tvInputTitle.text = "Enter New Email Address"
                    clMobile.isVisible = false
                    etEmail.isVisible = true
                    etEmail.setText(profileData?.email)
                }
                else{
                    toolbar.tvToolbar.text = "Change Mobile Number"
                    tvInputTitle.text = "Enter New Mobile Number"
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    etMobile.setText(profileData?.mobile)
                    tvCountryPicker.text = profileData?.countryCode
                }
            }
    }

    override fun initControl() {

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener{
                if (validation()) {
                    changeMobileOrEmail()
                }
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showSelectedCountryPicker(requireContext(),
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

        viewModel.updateAffiliateUserProfileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.getAffiliateProfileResult.observe(viewLifecycleOwner) { response ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                    CommonMethod.showSuccessDialog(requireActivity(),  if (from=="email")"Email Address Changed successfully !" else "Mobile Number Changed successfully !"){
                        findNavController().popBackStack()
                    }
                }
                isProfileObserver = false
            }
        }

    }

    private fun validation(): Boolean {
        binding?.apply {
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()

            return when {
                mobile.isEmpty()&&from=="mobile" -> {
                    showToast("Please enter new mobile number")
                    false
                }

                mobile.length < 7 &&from=="mobile"-> {
                    showToast("Please enter a valid new mobile number")
                    false
                }

                email.isEmpty() &&from=="email"-> {
                    showToast("Please enter new email address")
                    false
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() &&from=="email"-> {
                    showToast("Please enter a valid new email address")
                    false
                }

                else -> true
            }
        }
        return false
    }


    private fun changeMobileOrEmail(){
        val request = UpdateAffiliateProfileRequest(
            email = if (from=="email")binding?.etEmail?.text?.trim().toString()  else profileData?.email.toString(),
            mobile = if (from!="email")binding?.etMobile?.text?.trim().toString()  else profileData?.mobile.toString(),
            countryCode = if (from!="email")binding?.tvCountryPicker?.text?.trim().toString()  else profileData?.countryCode.toString(),
            fullName = profileData?.fullName.toString(),
            profileImage = profileData?.profileImage.toString(),
            document1 = profileData?.document1.toString(),
            document2 = profileData?.document2.toString(),
        )
        viewModel.updateAffiliateUserProfile(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAffiliateUserProfile(){
        viewModel.getAffiliateProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isProfileObserver = true
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getSelectedCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

}
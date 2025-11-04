package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentLoginBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var type = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        getCountry()
        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmailMobile)
            CommonMethod.removeAllTextSpaces(etPassword)
            tvCreateNewAccount.text = CommonMethod.spannableText(requireActivity(),requireActivity().getString(
                R.string.don_t_have_an_account_create_new),requireActivity().getString(R.string.signUp), onClick = {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)

            })
            tvCreateNewAccount.movementMethod = LinkMovementMethod.getInstance()
            tvCreateNewAccount.highlightColor = Color.TRANSPARENT

            etPassword.togglePasswordVisibility(ivPasswordVisible)
        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            }

            btnLogin.setOnClickListener {
                if (validation()) {
                    if (btnLoginViaOtp.text == "Login via Password") {
                        loginWithEMailOrMobile()
                    } else {
                        loginWithPassword()
                    }
                }
            }

            btnLoginViaOtp.setOnClickListener {
                if(btnLoginViaOtp.text == "Login via Password"){
                    tvPassword.isVisible = true
                    relPassword.isVisible = true
                    tvForgotPassword.isVisible = true
                    btnLogin.text = "Login"
                    btnLoginViaOtp.text = "Login via OTP"
                }
                else {
                    tvPassword.isVisible = false
                    relPassword.isVisible = false
                    tvForgotPassword.isVisible = false
                    btnLogin.text = "Continue"
                    btnLoginViaOtp.text = "Login via Password"
                }
            }

            tvGuest.setOnClickListener {
                Constants.isGuest = true
                requireActivity().startActivity(
                    Intent(requireActivity(),
                        HomeActivity::class.java)
                )
                requireActivity().finish()
            }

            ivGoogle.setOnClickListener {
            }

            ivFacebook.setOnClickListener {
            }

                etEmailMobile.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val input = s.toString().trim()
                        if (input.isNotEmpty()) {
                            if (input.matches(Regex("^[0-9]+$"))) {
                                type = 0
                                ccp.isVisible = true
                                ivEmail.setImageResource(R.drawable.ic_mobile)
                                etEmailMobile.inputType = InputType.TYPE_CLASS_NUMBER
                            } else {
                                type = 1
                                ccp.isVisible = false
                                ivEmail.setImageResource(R.drawable.ic_email)
                                etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            }
                        } else {
                            type = 1
                            ccp.isVisible = false
                            ivEmail.setImageResource(R.drawable.ic_email)
                            etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })


        }
    }

    private fun loginWithPassword(){
        val map = HashMap<String,Any>()

        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        if (type==0){
            map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus?.trim().toString()
        }
        map["type"] = type
        map["password"] = binding?.etPassword?.text?.trim().toString()
        viewModel.login(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun loginWithEMailOrMobile(){
        val map = HashMap<String,Any>()
        if (type==0){
            map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus?.trim().toString()
        }
        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        map["type"] = type
        viewModel.loginViaEmailOrMobile(map)
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

        viewModel.loginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            HomeActivity::class.java)
                    )
                    requireActivity().finish()
                }
                isObserver = false
            }
        }
        viewModel.loginEmailMobileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    val bundle = Bundle().apply {
                        if (type==0){
                            putString("mobileNumber", binding?.etEmailMobile?.text.toString().trim())
                            putString("countryCode", binding?.ccp?.selectedCountryCodeWithPlus.toString().trim())
                        }
                        else{
                            putString("email", binding?.etEmailMobile?.text.toString().trim())
                        }
                        putString("from", "login")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_OtpFragment,bundle)
                }
                isObserver = false
            }
        }

        viewModel.countryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveCountryList(response.data)
                    setCountry(response.data)
                }
                isObserver = false
            }
        }
    }

    private fun validation():Boolean{

        if (binding?.etEmailMobile?.text?.isEmpty() == true) {
            showToast("Please enter mobile number or email address")
            return false
        }
        else if (binding?.etEmailMobile?.text?.length!! < 7&&type==0) {
            showToast("Number must be at least 7 digits long")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmailMobile?.text.toString()).matches() && type==1) {
            showToast("Please enter valid email")
            return false
        }
        else if (binding?.etPassword?.text?.isEmpty() == true&&binding?.btnLoginViaOtp?.text != "Login via Password") {
            showToast("Please enter password")
            return false
        }
        return true
    }

    private fun getCountry() {
        viewModel.getCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setCountry(data: List<GetCountryResponse.Data>?) {
        if (!data.isNullOrEmpty()) {
            val countryCodes = data.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccp?.setCustomMasterCountries(countryCodeString)
        }
    }

}
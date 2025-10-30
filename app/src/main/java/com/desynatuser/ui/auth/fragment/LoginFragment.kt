package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
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
                if(btnLoginViaOtp.text == "Login via Password"){
                    loginWithEMailOrMobile()
                }
                else{
                    loginWithPassword()


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
        }
    }

    private fun loginWithPassword(){
        val map = HashMap<String,Any>()
        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        map["password"] = binding?.etPassword?.text?.trim().toString()
        viewModel.login(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun loginWithEMailOrMobile(){
        val map = HashMap<String,Any>()
        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
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
                        putString("mobileNumber", binding?.etEmailMobile?.text.toString().trim())
                        putString("from", "login")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_OtpFragment,bundle)
                }
                isObserver = false
            }
        }
    }

}
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
                    val bundle = Bundle().apply {
                        putString("mobileNumber", binding?.etEmailMobile?.text.toString().trim())
                        putString("from", "login")
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_OtpFragment,bundle)
                }
                else{
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            HomeActivity::class.java)
                    )
                    requireActivity().finish()

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

            }

            ivGoogle.setOnClickListener {
            }

            ivFacebook.setOnClickListener {
            }
        }
    }

    override fun observe() {
    }
}
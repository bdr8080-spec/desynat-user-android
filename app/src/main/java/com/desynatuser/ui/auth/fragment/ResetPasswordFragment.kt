package com.desynatuser.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentResetPasswordBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment() {
    private var binding: FragmentResetPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Reset Password"
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }
    }

    override fun initControl() {
        userId = arguments?.getString("userId").toString()

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener{
                findNavController().popBackStack()
            }

            btnDone.setOnClickListener {
                if (validation()) {
                    val map = HashMap<String,Any>()
                    map["id"] = userId
                    map["password"] = binding?.etPassword?.text.toString().trim()
                    map["confirm_password"] = binding?.etConfirmPassword?.text.toString().trim()
                    viewModel.createPassword(map)
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    isObserver = true

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

        viewModel.createPasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.password_reset_successfully), false
                    ) {
                        findNavController().popBackStack(R.id.login, false)
                    }
                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (password.isEmpty()) {
            showToast("Please enter password")
            return false
        }
        else if (!CommonMethod.isValidPassword(password)) {
            CommonMethod.showAlertDialog(requireActivity(),"Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
          //  showToast("Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one number, and one special character.")
            return false
        }
        else if (confirmPassword.isEmpty() ) {
            showToast("Please enter confirm password")
            return false
        }
        else if (password!=confirmPassword) {
            showToast("Password and confirm password didn't match")
            return false
        }
        return true
    }

}
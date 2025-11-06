package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangePasswordBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {
    private var binding: FragmentChangePasswordBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater,container,false)
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

            toolbar.tvToolbar.text = "Change Password"
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etOldPassword)

            etOldPassword.togglePasswordVisibility(ivOldPasswordVisible)
            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }

    }

    override fun initControl() {

        binding?.apply {

                toolbar.tvToolbar.setOnClickListener{
                    findNavController().popBackStack()
                }

            btnChange.setOnClickListener {
                    if (validation()) {
                        val map = HashMap<String,Any>()
                        map["old_password"] = binding?.etOldPassword?.text.toString().trim()
                        map["new_password"] = binding?.etPassword?.text.toString().trim()
                        map["confirm_password"] = binding?.etConfirmPassword?.text.toString().trim()
                        viewModel.changePassword(map)
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

        viewModel.changePasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(requireActivity(),"Password changed successfully!"){
                        findNavController().popBackStack()
                    }

                }
                isObserver = false
            }
        }
    }

    private fun validation():Boolean{
        val oldPassword = binding?.etOldPassword?.text.toString().trim()
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (oldPassword.isEmpty()) {
            showToast("Please enter old password")
            return false
        }
        else if (password.isEmpty()) {
            showToast("Please enter new password")
            return false
        }
        else if (confirmPassword.isEmpty() ) {
            showToast("Please enter confirm password")
            return false
        }
        else if (password!=confirmPassword) {
            showToast("New password and confirm password didn't match")
            return false
        }
        return true
    }

}
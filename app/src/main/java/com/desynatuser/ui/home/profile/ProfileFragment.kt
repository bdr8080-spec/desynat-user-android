package com.desynatuser.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProfileBinding
import com.desynatuser.ui.auth.AuthActivity
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var binding: FragmentProfileBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var user: LoginResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
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
            if (Constants.isGuest) {
                tvEmail.isVisible = false
                ivEdit.isVisible = false
            } else {
                user = SharedPreference.get(requireActivity()).getUser()
                tvEmail.text = user?.email
                tvName.text = user?.fullName
                ivProfileImage.load(user?.profileImage) {
                    crossfade(true)
                    placeholder(R.drawable.ic_user)
                    error(R.drawable.ic_user)
                }
            }
        }

    }

    override fun initControl() {

        binding?.apply {

            ivEdit.setOnClickListener{
                findNavController().navigate(R.id.action_profileFragment_to_profileDetailsFragment)
            }

            relLogout.setOnClickListener {
                if(Constants.isGuest){
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            AuthActivity::class.java)
                    )
                    requireActivity().finish()
                }
                else{
                     logout()
                  /*  ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    Handler(Looper.getMainLooper()).postDelayed({
                        ProgressDialogUtils.getInstance().hideProgress()
                        Constants.isLogout = true
                        CommonMethod.clearData(requireActivity())
                        requireActivity().startActivity(
                            Intent(requireActivity(),
                                AuthActivity::class.java)
                        )
                        requireActivity().finish()
                    }, 500) // delay in milliseconds*/
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

        viewModel.logoutResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                response?.let {
                    Constants.isLogout = true
                    CommonMethod.clearData(requireActivity())
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            AuthActivity::class.java)
                    )
                    requireActivity().finish()
                }
                isObserver = false
            }
        }
    }

   private fun logout(){
        viewModel.logout()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true

    }


}
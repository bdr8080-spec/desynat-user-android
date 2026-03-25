package com.desynatuser.ui.affiliateUser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateHomeBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateHomeFragment : BaseFragment() {
    private var binding: FragmentAffiliateHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isNotificationObserver = false
    var profileData : GetAffiliateUserProfileResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        if (SharedPreference.get(requireActivity()).getAffiliateUser()==null){
            getAffiliateUserProfile()
        }
        else{
            profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
            setData()
        }

        notificationCount()

    }

    private fun setData(){
        binding?.apply {
            tvName.text = "Hi ${profileData?.fullName}"
            Glide.with(requireActivity()).load(profileData?.profileImage).placeholder(R.drawable.ic_user).into(ivProfile)
        }
    }

    override fun initControl() {
        binding?.apply {
            ivNotification.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateHomeFragment_to_notificationFragment)
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isNotificationObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isNotificationObserver = false
            }
        }


        viewModel.affiliateUserNotificationCountResult.observe(viewLifecycleOwner) { response ->
            if (isNotificationObserver) {

                response?.let {
                    if (response?.data!=null&&response?.data!=0){
                        setNotificationCount(response.data)
                    }
                }
                isNotificationObserver = false
            }
        }

        viewModel.getAffiliateProfileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                profileData = response.data
                SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                setData()
                isObserver = false
            }
        }

    }

    private fun getAffiliateUserProfile(){
        viewModel.getAffiliateProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun notificationCount(){
        viewModel.affiliateUserNotificationCount()
        isNotificationObserver = true
    }

    private fun setNotificationCount(data: Int) {
        binding?.relCount?.isVisible = true
        binding?.tvCount?.text = data.toString()
    }
}
package com.desynatuser.ui.home.cms

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.DeleteAccountDialogBinding
import com.desynatuser.databinding.FragmentSettingBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.auth.AuthActivity
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.text.get

@AndroidEntryPoint
class SettingFragment : BaseFragment() {
    private var binding: FragmentSettingBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var userInfo: LoginResponse.Data?=null
    private var affiliateUserInfo: GetAffiliateUserProfileResponse.Data? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            affiliateUserInfo = SharedPreference.get(requireActivity()).getAffiliateUser()
        }
        else{
            userInfo = SharedPreference.get(requireActivity()).getUser()
        }
        setData()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.settings)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDeleteAccount.setOnClickListener {
                deleteAlertDialog()
            }


            tvInAppNotification.setOnClickListener {
                appNotificationOnOff()
            }

            tvPushNotification.setOnClickListener {
                notificationOnOff()
            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.notificationOnOffResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    userInfo?.notification = response?.data?.notification?:0
                    userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }

        viewModel.appNotificationOnOffResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    userInfo?.appNotification = response?.data?.appNotification?:0
                    userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }

        viewModel.notificationOnOffAffiliateResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    affiliateUserInfo?.notification = response?.data?.notification?:0
                    affiliateUserInfo?.let { SharedPreference.get(requireActivity()).saveAffiliateUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }

        viewModel.appNotificationOnOffAffiliateResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    affiliateUserInfo?.appNotification = response?.data?.appNotification?:0
                    affiliateUserInfo?.let { SharedPreference.get(requireActivity()).saveAffiliateUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }

        viewModel.affiliateDeleteAccountResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString()){
                        Constants.isLogout = true
                        CommonMethod.clearAffiliateData(requireActivity())
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                AuthActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }
                }
                isObserver = false
            }
        }


        viewModel.deleteAccountResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString()){
                        Constants.isGuest = false
                        Constants.isLogout = true
                        Constants.Category = "Women"
                        requireActivity().startActivity(
                            Intent(requireActivity(),
                                AuthActivity::class.java)
                        )
                        requireActivity().finish()
                    }
                    }
                isObserver = false
            }
        }

    }

    fun deleteAlertDialog(
    ) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DeleteAccountDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)


        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            deleteAccount()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun notificationOnOff(){
        val map = HashMap<String, Any>()
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.notificationOnOffAffiliate(map)
        }
        else{
            viewModel.notificationOnOff(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true

    }

    fun deleteAccount(){
        val map = HashMap<String, Any>()
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.affiliateDeleteAccount(map)
        }
        else{
            viewModel.deleteAccount(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true

    }

    fun appNotificationOnOff(){
        val map = HashMap<String, Any>()
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.appNotificationOnOffAffiliate(map)
        }
        else{
            viewModel.appNotificationOnOff(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true

    }

    fun setData(){
        binding?.apply {
            if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
                if(affiliateUserInfo?.appNotification==1){
                    tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_on, 0 )
                }
                else{
                    tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_off,0 )
                }

                if(affiliateUserInfo?.notification==1){
                    tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_on,0 )
                }
                else{
                    tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_off,0 )
                }
            }
            else{
                if(userInfo?.appNotification==1){
                    tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_on, 0 )
                }
                else{
                    tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_off,0 )
                }

                if(userInfo?.notification==1){
                    tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_on,0 )
                }
                else{
                    tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_off,0 )
                }
            }

        }
    }

}

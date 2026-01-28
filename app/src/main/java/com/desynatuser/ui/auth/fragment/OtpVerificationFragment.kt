package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentOtpVerificationBinding
import com.desynatuser.ui.affiliateUser.AffiliateHomeActivity
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment() {
    private var binding: FragmentOtpVerificationBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    var isObserver = false
    var mobileNumber = ""
    var countryCode = ""
    var email = ""
    var from = ""
    var userId = ""
    var screenType = -1 //0-signup,1-login,2-forgot
    var type = 0 //0-mobile,1-email
    lateinit var timer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        startTimer()
        mobileNumber = arguments?.getString("mobileNumber").toString()
        countryCode = arguments?.getString("countryCode").toString()
        from = arguments?.getString("from").toString()
        email = arguments?.getString("email").toString()
        userId = arguments?.getString("userId").toString()

        when(from){
            "signUp" -> screenType = 1
            "affiliateSignUp" -> {
                screenType = 1
                binding?.llTab?.isVisible = true
            }
            "affiliateLogin" -> screenType = 1
            "login" -> screenType = 1
            "forgot" -> screenType = 2
            "affiliateForgot" -> screenType = 2
        }
        binding?.apply {
            toolbar.tvToolbar.text = "OTP Verification"
              if (email!=""&&email!="null"){
                type = 1
                  tvMobile.text = email
            }
            else{
                type = 0
                  tvMobile.text = "${countryCode} ${mobileNumber}"
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvResendOtp.setOnClickListener {
                resendOtp()
            }

            btnVerify.setOnClickListener {
                val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast("Please enter OTP")
                    }
                    otp.length < 6 -> {
                        showToast("Please enter a valid 6-digit OTP")
                    }
                    else -> {
                        if (screenType==-1){
                            verifyUserOtp()
                        }
                        else{
                            if (from=="affiliateSignUp"||from=="affiliateLogin"||from=="affiliateForgot"){
                                verifyAffiliateUserOtp()
                            }
                            else{
                                verifyOtp()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun verifyUserOtp(){
        val map = HashMap<String,Any>()
        map["id"] = userId
        map["otp"] = binding?.pinView?.text?.trim().toString()
        homeViewModel.userOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun verifyOtp(){
        val map = HashMap<String,Any>()
        map["user_id"] = userId
        map["otp"] = binding?.pinView?.text?.trim().toString()
        map["type"] = screenType
        viewModel.otpVerification(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun verifyAffiliateUserOtp(){
        val map = HashMap<String,Any>()
        map["id"] = userId
        map["otp"] = binding?.pinView?.text?.trim().toString()
        map["type"] = "1"
        viewModel.verifyAffiliateUserOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun resendOtp(){
        val map = HashMap<String,Any>()
        if (email!=""&&email!="null"){
            map["email_mobile"] = email
        }
        else{
            map["email_mobile"] = mobileNumber
            map["country_code"] = countryCode
        }
        map["type"] = type
        if (from=="affiliateSignUp"||from=="affiliateLogin"){
            viewModel.resendOtpAffiliateUser(map)
        }
        else {
            viewModel.resendOtp(map)
        }
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

        homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.otpVerifyAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when(from){
                        "affiliateLogin" -> {
                           // SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                            showToast("OTP verified successfully")
                            requireActivity().startActivity(
                                Intent(requireActivity(),
                                    AffiliateHomeActivity::class.java)
                            )
                            requireActivity().finish()
                        }
                        "affiliateSignUp" -> {
                          //  SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_affiliateAddAddressFragment,bundle)
                        }
                        "affiliateForgot" -> {
                            showToast("OTP verified successfully")
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                                putString("from", from)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)
                        }
                        "affiliateEdit" -> {
                            showToast("OTP verified successfully")
                            val bundle = Bundle().apply {
                                putString("from", if (type==1)"email" else "mobile")
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_changeMobileFragment,bundle)
                        }
                    }

                }
                isObserver = false
            }
        }

        viewModel.otpVerifyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when(from){
                        "login" -> {
                            SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveUser(response.data)
                            showToast("OTP verified successfully")
                            requireActivity().startActivity(
                                Intent(requireActivity(),
                                    HomeActivity::class.java)
                            )
                            requireActivity().finish()
                        }
                        "signUp" -> {
                            SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveUser(response.data)
                            CommonMethod.showSuccessDialog(requireActivity(),"Thank you for sinning up with DESYNAT"){
                               findNavController().navigate(R.id.action_otpFragment_to_bannerFragment)
//                                requireActivity().startActivity(
//                                    Intent(requireActivity(),
//                                        HomeActivity::class.java)
//                                )
//                                requireActivity().finish()
                            }
                        }
                        "forgot" -> {
                            showToast("OTP verified successfully")
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)
                        }
                        "edit" -> {
                            showToast("OTP verified successfully")
                            val bundle = Bundle().apply {
                                putString("from", if (type==1)"email" else "mobile")
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_changeMobileFragment,bundle)
                        }
                    }

                }
                isObserver = false
            }
        }

        homeViewModel.userOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when(from){
                        "change" -> {
                            val data = SharedPreference.get(requireActivity()).getUser()
                            if (type==1) data?.email = response.data.email
                            else{ data?.mobile = response.data.mobile
                                data?.countryCode = response.data.countryCode}
                            if (data != null) {
                                SharedPreference.get(requireActivity()).saveUser(data)
                            }
                            CommonMethod.showSuccessDialog(requireActivity(),  if (from=="email")"Email Address Changed successfully !" else "Mobile Number Changed successfully !"){
                                findNavController().popBackStack(R.id.profileDetails, false)
                            }
                        }
                        "edit" -> {
                            showToast("OTP verified successfully")
                            val bundle = Bundle().apply {
                                putString("from", if (type==1)"email" else "mobile")
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_changeMobileFragment,bundle)
                        }
                    }

                }
                isObserver = false
            }
        }

        viewModel.resendOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (type==0){
                    showToast("OTP sent to mobile number successfully")
                }
                else{
                    showToast("OTP sent to email address successfully")
                }
                response?.let {
                    startTimer()

                }
                isObserver = false
            }
        }

        viewModel.resendOtpAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (type==0){
                    showToast("OTP sent to mobile number successfully")
                }
                else{
                    showToast("OTP sent to email address successfully")
                }
                response?.let {
                    startTimer()

                }
                isObserver = false
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding?.tvResendOtp?.text = "${seconds} Sec"
                binding?.tvResendOtp?.isClickable = false
            }

            override fun onFinish() {
                binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                binding?.tvResendOtp?.isClickable = true
            }
        }.start()
    }

 /*   private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) { // 2 minutes = 120000 milliseconds
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding?.tvResendOtp?.text = makeMeTwoDigits(minutes, seconds)
                binding?.tvResendOtp?.isClickable = false
            }

            override fun onFinish() {
                binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                binding?.tvResendOtp?.isClickable = true
            }
        }.start()
    }

    fun makeMeTwoDigits(minutes: Long, seconds: Long):String {
        val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
        val formattedSeconds = if (seconds < 10) "0$seconds" else seconds.toString()

        return "${formattedMinutes}:${formattedSeconds}"

    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}
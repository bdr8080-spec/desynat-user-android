package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentOtpVerificationBinding
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
    var isObserver = false
    var mobileNumber = ""
    var countryCode = ""
    var email = ""
    var from = ""
    var userId = ""
    var type = 0 //0-signup,1-login,2-forgot
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
            "signUp" -> type = 0
            "login" -> type = 1
            "forgot" -> type = 2
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Otp Verification"
            tvMobile.text =  if (email!=""&&email!="null"){
                email
            }
            else{
                "${countryCode} ${mobileNumber}"
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
                        verifyOtp()
                    }
                }
            }
        }

    }

    private fun verifyOtp(){
        val map = HashMap<String,Any>()
        map["user_id"] = userId
        map["otp"] = binding?.pinView?.text?.trim().toString()
        map["type"] = type
        viewModel.otpVerification(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun resendOtp(){
        val map = HashMap<String,Any>()
        map["email_mobile"] = if (email!=""&&email!="null"){
            email
        }
        else{
            "${countryCode} ${mobileNumber}"
        }
        viewModel.otpVerification(map)
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

        viewModel.otpVerifyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    when(from){
                        "login" -> {
                            SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveUser(response.data)
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
                                requireActivity().startActivity(
                                    Intent(requireActivity(),
                                        HomeActivity::class.java)
                                )
                                requireActivity().finish()
                            }
                        }
                        "forgot" -> {
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)
                        }
                    }

                }
                isObserver = false
            }
        }

        viewModel.resendOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
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
}
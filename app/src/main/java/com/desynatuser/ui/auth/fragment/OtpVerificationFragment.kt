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
                startTimer()
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
                        when(from){
                            "login" -> {
                                requireActivity().startActivity(
                                    Intent(requireActivity(),
                                        HomeActivity::class.java)
                                )
                                requireActivity().finish()
                            }
                            "signUp" -> {
                                CommonMethod.showSuccessDialog(requireActivity(),"Thank you for sinning up with DESYNAT"){
                                    requireActivity().startActivity(
                                        Intent(requireActivity(),
                                            HomeActivity::class.java)
                                    )
                                    requireActivity().finish()
                                }
                            }
                            "forgot" -> {
                                findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment)
                            }
                        }

                    }
                }
            }
        }

    }

    override fun observe() {
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
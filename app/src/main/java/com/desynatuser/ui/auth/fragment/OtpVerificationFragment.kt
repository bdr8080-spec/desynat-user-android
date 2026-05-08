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
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
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
        setupOtpInputs()
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
            toolbar.tvToolbar.text = getString(R.string.otp_verification)
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
                val otp = if (
                    resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
                ) {

                    binding?.etOtp6?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp1?.text.toString()

                } else {

                    binding?.etOtp1?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp6?.text.toString()
                }

                when {
                    otp.isEmpty() -> {
                        showToast(getString(R.string.please_enter_otp))
                    }
                    otp.length < 6 -> {
                        showToast(getString(R.string.please_enter_a_valid_6_digit_otp))
                    }
                    else -> {
                        if (screenType == -1) {
                            when (from) {
                                "edit", "change" -> verifyUserOtp(otp)
                                "affiliateEdit", "affiliateChange" -> affiliateUserOtp(otp)
                            }
                        }
                        else{
                            if (from=="affiliateSignUp"||from=="affiliateLogin"||from=="affiliateForgot"){
                                SharedPreference.get(requireActivity()).affiliateAccessToken = ""
                                verifyAffiliateUserOtp(otp)
                            }
                            else{
                                verifyOtp(otp)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun verifyUserOtp(otp: String){
        val map = HashMap<String,Any>()
        map["id"] = userId
        map["otp"] = otp
        homeViewModel.userOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun verifyOtp(otp: String){
        val map = HashMap<String,Any>()
        map["user_id"] = userId
        map["otp"] = otp
        map["type"] = screenType
        if (screenType==1){
            map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
        }

        viewModel.otpVerification(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun affiliateUserOtp(otp: String){
        val map = HashMap<String,Any>()
        map["id"] = userId
        map["otp"] = otp
        homeViewModel.affiliateUserOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun verifyAffiliateUserOtp(otp: String){
        val map = HashMap<String,Any>()
        map["id"] = userId
        map["otp"] = otp
        map["type"] = "1"
        map["device_type"] = Constants.DEVICE_TYPE
        if (from=="affiliateLogin"){
            map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
        }
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
                    SharedPreference.get(requireActivity()).affiliateAccessToken = response.data.accessToken
                    when(from){
                        "affiliateLogin" -> {
                            when(response.data.adminVerify){
                                "0"->{
                                    when{
                                        response.data.addressUpdateStatus=="0" ->{
                                            findNavController().navigate(R.id.action_otpFragment_to_affiliateAddAddressFragment)
                                        }
                                        response.data.bankUpdateStatus=="0" ->{
                                            findNavController().navigate(R.id.action_otpFragment_to_affiliateAddBankDetailFragment)
                                        }
                                    }
                                }
                                "1"->{
                                    CommonMethod.showSuccessWithTimeDialog(requireActivity(),
                                        getString(
                                            R.string.your_profile_is_under_review
                                        ), time = 2000){
                                    }
                                }
                                "2"->{
                                    SharedPreference.get(requireActivity()).isAffiliateTutorial = true
                                 //   SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                                    showToast(getString(R.string.otp_verified_successfully))
                                    requireActivity().startActivity(
                                        Intent(requireActivity(),
                                            AffiliateHomeActivity::class.java)
                                    )
                                    requireActivity().finish()
                                }
                                "3"->{
                                    CommonMethod.showSuccessWithTimeDialog(requireActivity(),
                                        "${getString(
                                            R.string.your_profile_is_rejected_by_admin)} (${response.data.rejectReason})"
                                        , time = 3000){

                                    }
                                }
                                "4"->{
                                    CommonMethod.showSuccessWithTimeDialog(requireActivity(),
                                        "${getString(
                                            R.string.document_rejected_please_reupload_it)} (${response.data.rejectReason})"
                                       , time = 3000){
                                        val bundle = Bundle().apply {
                                            putString("signUpData", Gson().toJson(response.data))
                                           // putString("password",binding?.etPassword?.text?.toString())
                                        }
                                        findNavController().navigate(R.id.action_otpFragment_to_affiliateSignUpFragment,bundle)
                                    }
                                }
                            }

                        }
                        "affiliateSignUp" -> {
                         //   SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                            SharedPreference.get(requireActivity()).isAffiliateTutorial = true

                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_affiliateAddAddressFragment,bundle)
                        }
                        "affiliateForgot" -> {
                            showToast(getString(R.string.otp_verified_successfully))
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                                putString("from", from)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)
                        }
                        "affiliateEdit" -> {
                            showToast(getString(R.string.otp_verified_successfully))
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
                            showToast(getString(R.string.otp_verified_successfully))
                            requireActivity().startActivity(
                                Intent(requireActivity(),
                                    HomeActivity::class.java)
                            )
                            requireActivity().finish()
                        }
                        "signUp" -> {
                            SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).saveUser(response.data)
                            CommonMethod.showSuccessDialog(requireActivity(),
                                getString(R.string.thank_you_for_sinning_up_with_desynat)){
                               findNavController().navigate(R.id.action_otpFragment_to_bannerFragment)
//                                requireActivity().startActivity(
//                                    Intent(requireActivity(),
//                                        HomeActivity::class.java)
//                                )
//                                requireActivity().finish()
                            }
                        }
                        "forgot" -> {
                            showToast(getString(R.string.otp_verified_successfully))
                            val bundle = Bundle().apply {
                                putString("userId", response.data.id)
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)
                        }
                        "edit" -> {
                            showToast(getString(R.string.otp_verified_successfully))
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
                            CommonMethod.showSuccessDialog(requireActivity(),  if (from=="email") getString(
                                R.string.email_address_changed_successfully
                            ) else getString(R.string.mobile_number_changed_successfully)){
                                findNavController().popBackStack(R.id.profileDetails, false)
                            }
                        }
                        "edit" -> {
                            showToast(getString(R.string.otp_verified_successfully))
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

        homeViewModel.affiliateUserOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when(from){
                        "affiliateChange" -> {
                            val data = SharedPreference.get(requireActivity()).getAffiliateUser()
                            if (type==1) data?.email = response.data.email
                            else{ data?.mobile = response.data.mobile
                                data?.countryCode = response.data.countryCode}
                            if (data != null) {
                                SharedPreference.get(requireActivity()).saveAffiliateUser(data)
                            }
                            CommonMethod.showSuccessDialog(requireActivity(),  if (from=="email")getString(R.string.email_address_changed_successfully) else getString(R.string.mobile_number_changed_successfully)){
                                findNavController().popBackStack(R.id.affiliateProfileDetail, false)
                            }
                        }
                        "affiliateEdit" -> {
                            showToast(getString(R.string.otp_verified_successfully))
                            val bundle = Bundle().apply {
                                putString("from", if (type==1)"email" else "mobile")
                            }
                            findNavController().navigate(R.id.action_otpFragment_to_affiliateChangeMobileFragment,bundle)
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
                    showToast(getString(R.string.otp_sent_to_mobile_number_successfully))
                }
                else{
                    showToast(getString(R.string.otp_sent_to_email_address_successfully))
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
                    showToast(getString(R.string.otp_sent_to_mobile_number_successfully))
                }
                else{
                    showToast(getString(R.string.otp_sent_to_email_address_successfully))
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

    private fun setupOtpInputs() {

        val otpFields = listOf(
            binding?.etOtp1,
            binding?.etOtp2,
            binding?.etOtp3,
            binding?.etOtp4,
            binding?.etOtp5,
            binding?.etOtp6
        )

        val isArabic =
            resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        // First focus
        if (isArabic) {
            binding?.etOtp6?.requestFocus()
        } else {
            binding?.etOtp1?.requestFocus()
        }

        otpFields.forEachIndexed { index, editText ->

            editText?.addTextChangedListener(object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: android.text.Editable?) {

                    if (s?.length == 1) {

                        if (isArabic) {

                            // Move LEFT in Arabic
                            if (index > 0) {
                                otpFields[index - 1]?.requestFocus()
                            }

                        } else {

                            // Move RIGHT in English
                            if (index < otpFields.size - 1) {
                                otpFields[index + 1]?.requestFocus()
                            }
                        }
                    }
                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->

                if (event.action == android.view.KeyEvent.ACTION_DOWN &&
                    keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    editText.text.isEmpty()
                ) {

                    if (isArabic) {

                        // Back move RIGHT in Arabic
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1]?.requestFocus()
                        }

                    } else {

                        // Back move LEFT in English
                        if (index > 0) {
                            otpFields[index - 1]?.requestFocus()
                        }
                    }
                }

                false
            }
        }
    }

}
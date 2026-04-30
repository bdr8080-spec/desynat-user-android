package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentSignUpBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.RegisterRequest
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.CustomTypefaceSpan
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.text.get

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {
    private var binding: FragmentSignUpBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var isImageObserver = false
    private lateinit var imagePicker: ImagePicker
    private var profileImage: File? = null
    private var profileImageUrl:String?=null
    var countryList = ArrayList<GetCountryResponse.Data>()

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_ERROR", "Token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
            SharedPreference.get(requireActivity()).deviceToken = token
        }
        setCountry()
        binding?.apply {
            CommonMethod.removeSpace(etFullName)
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etPassword)
            tvAlreadyAccount.text = CommonMethod.spannableText(requireActivity(),requireActivity().getString(
                R.string.already_have_an_account_sign_in),requireActivity().getString(R.string.sign_in), onClick = {
                findNavController().popBackStack()

            })
            tvAlreadyAccount.movementMethod = LinkMovementMethod.getInstance()
            tvAlreadyAccount.highlightColor = Color.TRANSPARENT

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
            tvTermsPolicy.text = spannableText()
            tvTermsPolicy?.movementMethod = LinkMovementMethod.getInstance()
            tvTermsPolicy?.highlightColor = Color.TRANSPARENT

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    profileImage = CommonMethod.uriToFile(requireActivity(), it)
                    ivProfile.setImageURI(it)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            ivProfile.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showUserCountryPicker(requireContext(),
                    clMobile,
                    countryList,true
                ) { selectedCountry ->
                    tvCountryPicker?.text = selectedCountry.dialCode
                }
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    if (profileImage!=null){
                        profileImage?.let { it1 -> uploadFile(it1) }
                    }
                    else{
                        createAccount()
                    }

                }

            }

            tvGuest.setOnClickListener {
                Constants.isGuest = true
                CommonMethod.clearData(requireActivity())
                CommonMethod.clearAffiliateData(requireActivity())
                Constants.Category = "Women"
                requireActivity().startActivity(
                    Intent(requireActivity(),
                        HomeActivity::class.java)
                )
                requireActivity().finish()
            }
        }
    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    fun createAccount(){
        binding?.apply {
            val request = RegisterRequest(
                email = etEmail.text.toString().trim(),
                mobile = etMobile.text.toString().trim(),
                countryCode = tvCountryPicker.text.toString(),
                fullName = etFullName.text.toString().trim(),
                password = etPassword.text.toString().trim(),
                confirmPassword = etConfirmPassword.text.toString().trim(),
                profileImage = profileImageUrl,
                lang = SharedPreference.get(requireActivity()).language
            )
            viewModel.register(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
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

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    profileImageUrl = response.data.url
                    createAccount()
                }
                isImageObserver = false
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.otp_sent_successfully_to_your_mobile_number)
                    ) {
                    val bundle = Bundle().apply {
                        putString("countryCode", binding?.tvCountryPicker?.text.toString())
                        putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                        putString("from", "signUp")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_OtpFragment,
                        bundle
                    )
                }
                }
                isObserver = false
            }
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            val fullName = etFullName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            return when {
                fullName.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_full_name))
                    false
                }

                mobile.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_mobile_number))
                    false
                }

                mobile.length < 7 -> {
                    showToast(getString(R.string.please_enter_a_valid_mobile_number))
                    false
                }

                email.isEmpty() -> {
                    showToast(getString(R.string.please_enter_your_email_address))
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast(getString(R.string.please_enter_a_valid_email_address))
                    false
                }

                password.isEmpty() -> {
                    showToast(getString(R.string.please_enter_a_password))
                    false
                }

                !CommonMethod.isValidPassword(password) -> {
                    CommonMethod.showAlertDialog(requireActivity(),
                        getString(R.string.password_must_be_at_least_8_characters_long_and_include_letters_and_digits))
                    //  showToast("Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one number, and one special character.")
                    false
                }

                confirmPassword.isEmpty() -> {
                    showToast(getString(R.string.please_enter_confirm_password))
                    false
                }

                confirmPassword != password -> {
                    showToast(getString(R.string.password_and_confirm_password_didn_t_match))
                    false
                }

                !cbTermsPolicy.isChecked -> {
                    showToast(getString(R.string.please_accept_terms_policies_to_continue))
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun spannableText(
    ): SpannableString {
        val textSpannable = SpannableString(getString(R.string.i_accept_the_terms_conditions_and_policies))
        val typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_regular)

        fun applySpan(target: String, click: (() -> Unit)?) {
            val startIndex = textSpannable.indexOf(target)
            if (startIndex != -1) {
                val endIndex = startIndex + target.length

                typeface?.let {
                    textSpannable.setSpan(
                        CustomTypefaceSpan(null, it),
                        startIndex, endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                textSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.brown)),
                    startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                click?.let {
                    textSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                it()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = true
                                ds.color = ContextCompat.getColor(requireActivity(),  R.color.brown)
                            }
                        },
                        startIndex, endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        applySpan(getString(R.string.terms_conditions)){
            val bundle = Bundle().apply {
                putInt("type", 1)
            }
            findNavController().navigate(R.id.action_signUpFragment_to_cmsContentFragment,bundle)
        }
        applySpan(getString(R.string.policies)){

        }

        return textSpannable
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        data?.let { countryList.addAll(it) }
    }

}
package com.desynatuser.ui.auth.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
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
import com.desynatuser.ui.auth.response.RegisterRequest
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.CustomTypefaceSpan
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {
    private var binding: FragmentSignUpBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var isImageObserver = false
    private lateinit var imagePicker: ImagePicker
    private var profileImage: File? = null
    private var profileImageUrl:String?=null
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
      //  setCountry()
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
            cbTermsPolicy.text = spannableText()

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
                countryCode = ccp.selectedCountryCodeWithPlus,
                fullName = etFullName.text.toString().trim(),
                password = etPassword.text.toString().trim(),
                confirmPassword = etConfirmPassword.text.toString().trim(),
                profileImage = profileImageUrl
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
                        "OTP sent successfully to your mobile number "
                    ) {
                    val bundle = Bundle().apply {
                        putString("countryCode", binding?.ccp?.selectedCountryCodeWithPlus)
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

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        if (!data.isNullOrEmpty()) {
            val countryCodes = data.map { it.isoCode }
            val countryCodeString = countryCodes.joinToString(",")
            binding?.ccp?.setCustomMasterCountries(countryCodeString)
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
                    showToast("Please enter your full name")
                    false
                }

                mobile.isEmpty() -> {
                    showToast("Please enter your mobile number")
                    false
                }

                mobile.length < 7 -> {
                    showToast("Please enter a valid mobile number")
                    false
                }

                email.isEmpty() -> {
                    showToast("Please enter your email address")
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast("Please enter a valid email address")
                    false
                }

                password.isEmpty() -> {
                    showToast("Please enter a password")
                    false
                }

               /* !CommonMethod.isValidPassword(password) -> {
                    CommonMethod.showAlertDialog(requireActivity(),"Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
//                    showToast("Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
                    false
                }*/

                confirmPassword.isEmpty() -> {
                    showToast("Please enter confirm password")
                    false
                }

                confirmPassword != password -> {
                    showToast("Password and confirm password didn't match")
                    false
                }

                !cbTermsPolicy.isChecked -> {
                    showToast("Please accept Terms & Policies to continue")
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

        }
        applySpan(getString(R.string.policies)){

        }

        return textSpannable
    }

}
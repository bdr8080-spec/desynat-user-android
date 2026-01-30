package com.desynatuser.ui.affiliateUser.auth

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
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateSignUpBinding
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserRequest
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.home.giftCard.response.BuyGiftCardResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.CustomTypefaceSpan
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AffiliateSignUpFragment : BaseFragment() {
    private var binding: FragmentAffiliateSignUpBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private var isObserver = false
    private var isImageObserver = false
    private var imageType = ""
    private lateinit var imagePicker: ImagePicker
    private var profileImageUrl:String?=null
    private var docUrl1:String?=null
    private var docUrl2:String?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    private var affiliateUserData : AffiliateUserLoginResponse.Data?=null

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
        binding = FragmentAffiliateSignUpBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            findNavController().navigate(
                R.id.login,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.login, false)
                    .build()
            )
        }

    }

    override fun initView() {
        val itemSignUpDataJson = arguments?.getString("signUpData")
        if (!itemSignUpDataJson.isNullOrEmpty()) {
            affiliateUserData = Gson().fromJson(itemSignUpDataJson, AffiliateUserLoginResponse.Data::class.java)
            setData()
        }

        if (SharedPreference.get(requireActivity()).getSelectedCountryList()==null){
            getSelectedCountry()
        }
        else{
            setCountry()
        }
        binding?.apply {
            CommonMethod.removeSpace(etFullName)
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etPassword)
            tvAlreadyAccount.text = CommonMethod.spannableText(requireActivity(),requireActivity().getString(
                R.string.already_have_an_account_sign_in),requireActivity().getString(R.string.sign_in), onClick = {
                findNavController().navigate(
                    R.id.login,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.login, inclusive = false)
                        .build()
                )

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
                    when(imageType){
                        "1"->{
                            ivProfile.setImageURI(it)
                            uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                        }
                        "2"->{
                            ivDoc1.setImageURI(it)
                            uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                        }
                        "3"->{
                            ivDoc2.setImageURI(it)
                            uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                        }
                    }

                }
            }
        }
    }

    private fun setData() {
        binding?.apply {
            etEmail.isClickable = false
            etEmail.isFocusable = false
            etEmail.isEnabled = false

            etMobile.isClickable = false
            etMobile.isFocusable = false
            etMobile.isEnabled = false

            tvMobile.isClickable = false

            etEmail.setText(affiliateUserData?.email)
            etFullName.setText(affiliateUserData?.fullName)
            etMobile.setText(affiliateUserData?.mobile)
            etPassword.setText(affiliateUserData?.passwordOld)
            etConfirmPassword.setText(affiliateUserData?.passwordOld)
            tvCountryPicker.text = affiliateUserData?.countryCode
            Glide.with(requireActivity()).load(affiliateUserData?.profileImage).placeholder(R.drawable.ic_placeholder_gallery).into(ivProfile)
            Glide.with(requireActivity()).load(affiliateUserData?.document1).placeholder(R.drawable.doc1).into(ivDoc1)
            Glide.with(requireActivity()).load(affiliateUserData?.document2).placeholder(R.drawable.doc2).into(ivDoc2)
            cbTermsPolicy.isChecked = true
            docUrl1 = affiliateUserData?.document1
            docUrl2 = affiliateUserData?.document2
            profileImageUrl = affiliateUserData?.profileImage
        }
    }

    override fun initControl() {
        binding?.apply {

            ivProfile.setOnClickListener {
                imageType = "1"
                imagePicker.showImageSourceDialog()
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showSelectedCountryPicker(requireContext(),
                    clMobile,
                    countryList,true
                ) { selectedCountry ->
                    tvCountryPicker?.text = selectedCountry.dialCode
                }
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    createAccount()
                    }
                }

            ivDoc1.setOnClickListener {
                imageType = "2"
                imagePicker.showImageSourceDialog()
            }

            ivDoc2.setOnClickListener {
                imageType = "3"
                imagePicker.showImageSourceDialog()
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
            val request = CreateAffiliateUserRequest(
                email = etEmail.text.toString().trim(),
                mobile = etMobile.text.toString().trim(),
                countryCode = tvCountryPicker.text.toString(),
                fullName = etFullName.text.toString().trim(),
                password = etPassword.text.toString().trim(),
                confirmPassword = etConfirmPassword.text.toString().trim(),
                profileImage = profileImageUrl.toString(),
                accept = 1,
                document1 = docUrl1.toString(),
                document2 = docUrl2.toString()
            )
            if (affiliateUserData!=null){
                viewModel.reVerifyDocument(request)
            }
            else {
                viewModel.createAffiliateUser(request)
            }
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

        viewModel.getSelectedCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveSelectedCountryList(response.data)
                    setCountry()
                }
                isObserver = false
            }
        }

        viewModel.reVerifyDocumentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                  CommonMethod.showSuccessDialog(requireActivity(),"Document submitted successfully"){
                      findNavController().navigate(
                          R.id.action_affiliateSignUpFragment_to_underReviewFragment
                      )
                  }
                }
                isObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when(imageType){
                        "1"->{
                            profileImageUrl = response.data.url
                        }
                        "2"->{
                            docUrl1 = response.data.url
                        }
                        "3"->{
                            docUrl2 = response.data.url
                        }
                    }

                }
                isImageObserver = false
            }
        }

        viewModel.createAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "OTP sent successfully to your mobile number "
                    ) {
                        val bundle = Bundle().apply {
                            putString("countryCode", binding?.tvCountryPicker?.text.toString())
                            putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                            putString("from", "affiliateSignUp")
                            putString("userId", response.data.id)
                        }
                        findNavController().navigate(
                            R.id.action_affiliateSignUpFragment_to_OtpFragment,
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
                profileImageUrl==null -> {
                    showToast("Please upload profile image")
                    false
                }
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

                docUrl1==null -> {
                    showToast("Please upload document1")
                    false
                }

                docUrl2==null -> {
                    showToast("Please upload document2")
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

                 !CommonMethod.isValidPassword(password) -> {
                     CommonMethod.showAlertDialog(requireActivity(),"Password must be at least 8 characters long and include letters and digits")
                     //   CommonMethod.showAlertDialog(requireActivity(),"Password must be at least 8 characters long and include uppercase, lowercase, number, and special symbol")
                     false
                 }

                confirmPassword.isEmpty() -> {
                    showToast("Please re-enter password")
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

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getSelectedCountryList()
        countryList.clear()
        data?.let { countryList.addAll(it) }
    }

    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}
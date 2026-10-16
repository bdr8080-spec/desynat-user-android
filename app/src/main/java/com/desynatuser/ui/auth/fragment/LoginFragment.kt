package com.desynatuser.ui.auth.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentLoginBinding
import com.desynatuser.ui.affiliateUser.AffiliateHomeActivity
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.home.HomeActivity
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.togglePasswordVisibility
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var type = 1
    var countryList = ArrayList<GetCountryResponse.Data>()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private var callbackManager: CallbackManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        printHashKey()
        printSha1Fingerprint()
        initView()
        initControl()
        observe()
    }

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {

                val account = task.getResult(ApiException::class.java)

                val idToken = account?.idToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e("GOOGLE_SIGN_IN", "Google ID token is null")
                    showToast("Google sign in failed: ID token is null")
                }

            } catch (e: ApiException) {
                Log.e("GOOGLE_SIGN_IN", "Google Sign-In failed with status code: ${e.statusCode}", e)
                if (e.statusCode == CommonStatusCodes.DEVELOPER_ERROR || e.statusCode == 10) {
                    Log.e(
                        "GOOGLE_SIGN_IN",
                        "DEVELOPER_ERROR (Status 10): SHA-1 fingerprint mismatch or invalid Web Client ID. " +
                                "Ensure your current build SHA-1 fingerprint is added in Firebase Console."
                    )
                    showToast("Google Sign-In failed (Status 10): Please add SHA-1 fingerprint in Firebase Console.")
                } else {
                    showToast("Google sign in failed (Error code: ${e.statusCode})")
                }
            } catch (e: Exception) {
                Log.e("GOOGLE_SIGN_IN", "Google Sign-In unexpected error", e)
                showToast(e.message ?: "Google login failed")
            }
        }

    override fun initView() {
        firebaseAuth = FirebaseAuth.getInstance() //firebase auth initialize

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task -> //get fcm token
            if (!task.isSuccessful) {
                Log.e("FCM_ERROR", "Token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
            SharedPreference.get(requireActivity()).deviceToken = token
        }

        if (SharedPreference.get(requireActivity()).userType==2){ //for affiliate user
            if (SharedPreference.get(requireActivity()).getSelectedCountryList()==null){
                getSelectedCountry()
            }
            else{
                setSelectedCountry()
            }
        }
        else {
            if (SharedPreference.get(requireActivity()).getCountryList() == null) {
                getCountry()
            } else {
                setCountry()
            }
        }

        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmailMobile)
            CommonMethod.removeAllTextSpaces(etPassword)
            tvCreateNewAccount.text = CommonMethod.spannableText(requireActivity(),requireActivity().getString(
                R.string.don_t_have_an_account_create_new),requireActivity().getString(R.string.signUp), onClick = {
               /* if (SharedPreference.get(requireActivity()).userType==2){
                    findNavController().navigate(R.id.action_loginFragment_to_affiliateSignUpFragment)
                }
                else{
                    findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
                }*/


            })
            tvCreateNewAccount.movementMethod = LinkMovementMethod.getInstance()
            tvCreateNewAccount.highlightColor = Color.TRANSPARENT

            tvCreateNewAccount.setOnClickListener {
                if (SharedPreference.get(requireActivity()).userType==2){
                    findNavController().navigate(R.id.action_loginFragment_to_affiliateSignUpFragment)
                }
                else{
                    findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
                }

            }

            etPassword.togglePasswordVisibility(ivPasswordVisible)

            if (SharedPreference.get(requireActivity()).userType==2){
                tvGuest.text = getString(R.string.switch_to_user)
                tvOtherOption.isInvisible = true
                view2.isInvisible = true
                view1.isInvisible = true
                ivFacebook.isInvisible = true
                ivGoogle.isInvisible = true
            }
            else{
                tvGuest.text = getString(R.string.continue_as_a_guest)
                view2.isVisible = true
                view1.isVisible = true
                ivFacebook.isVisible = true
                ivGoogle.isVisible = true
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            }

            tvCountryPicker.setOnClickListener {
                if (SharedPreference.get(requireActivity()).userType==2){
                    CommonMethod.showSelectedCountryPicker(
                        requireContext(),
                        clMobile,
                        countryList, true
                    ) { selectedCountry ->
                        tvCountryPicker?.text = selectedCountry.dialCode
                    }
                }
                else {
                    CommonMethod.showUserCountryPicker(
                        requireContext(),
                        clMobile,
                        countryList, true
                    ) { selectedCountry ->
                        tvCountryPicker?.text = selectedCountry.dialCode
                    }
                }
            }

            btnLogin.setOnClickListener {
                if (validation()) {
                    if (btnLoginViaOtp.text == getString(R.string.login_via_password)) {
                        loginWithEMailOrMobile()
                    } else {
                        loginWithPassword()
                    }
                }
            }

            btnLoginViaOtp.setOnClickListener {
                if(btnLoginViaOtp.text == getString(R.string.login_via_password)){
                    clearInputField()
                    tvPassword.isVisible = true
                    relPassword.isVisible = true
                    tvForgotPassword.isVisible = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginViaOtp.text = getString(R.string.login_via_otp)
                }
                else {
                    clearInputField()
                    tvPassword.isVisible = false
                    relPassword.isVisible = false
                    tvForgotPassword.isVisible = false
                    btnLogin.text = getString(R.string.continue_text)
                    btnLoginViaOtp.text = getString(R.string.login_via_password)
                }
            }

            tvGuest.setOnClickListener {
                if (SharedPreference.get(requireActivity()).userType==2){
                    SharedPreference.get(requireActivity()).userType = 1
                    tvGuest.text = getString(R.string.continue_as_a_guest)
                    clearInputField()
                    val intent = Intent(requireActivity(), HomeActivity::class.java).apply {
                        putExtra("open_screen", "profile")
                    }
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
                else{
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

            //google login
            ivGoogle.setOnClickListener {

                googleSignInClient.signOut().addOnCompleteListener {

                    val signInIntent = googleSignInClient.signInIntent
                    googleLauncher.launch(signInIntent)
                }
            }

            //facebook login
            ivFacebook.setOnClickListener {
                LoginManager.getInstance().logInWithReadPermissions(
                    this@LoginFragment,
                    listOf("email", "public_profile")
                )
            }

            LoginManager.getInstance()
                .registerCallback(
                    callbackManager,
                    object : FacebookCallback<LoginResult> {

                        override fun onSuccess(result: LoginResult) {

                            val token = result.accessToken.token

                            Log.e("FACEBOOK_TOKEN", token)

                            getFacebookUserData()
                        }

                        override fun onCancel() {

                            Log.e("FACEBOOK", "Cancelled")
                        }

                        override fun onError(error: FacebookException) {

                            Log.e("FACEBOOK", error.message.toString())
                        }
                    })

                etEmailMobile.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val input = s.toString().trim()
                        if (input.isNotEmpty()) {
                            if (input.matches(Regex("^[0-9]+$"))) {
                                type = 0
                                tvCountryPicker.isVisible = true
                                ivEmail.setImageResource(R.drawable.ic_mobile)
                                etEmailMobile.inputType = InputType.TYPE_CLASS_NUMBER
                            } else {
                                type = 1
                                tvCountryPicker.isVisible = false
                                ivEmail.setImageResource(R.drawable.ic_email)
                                etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            }
                        } else {
                            type = 1
                            tvCountryPicker.isVisible = false
                            ivEmail.setImageResource(R.drawable.ic_email)
                            etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
        }
    }

    private fun clearInputField() {
        binding?.apply {
            etEmailMobile.setText("")
            etPassword.setText("")
        }
    }

    private fun loginWithPassword(){
        val map = HashMap<String,Any>()

        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        if (type==0){
            map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
        }
        map["type"] = type
        map["password"] = binding?.etPassword?.text?.trim().toString()
        map["lang"] = SharedPreference.get(requireActivity()).language
        map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
        map["device_type"] = Constants.DEVICE_TYPE
        if (SharedPreference.get(requireActivity()).userType==2){
            viewModel.loginAffiliateUser(map)
        }
        else{
            viewModel.login(map)
        }

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun loginWithEMailOrMobile(){
        val map = HashMap<String,Any>()


        if (SharedPreference.get(requireActivity()).userType==2){
            if (type==0){
                map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
                map["mobile"] = binding?.etEmailMobile?.text?.trim().toString()
            }
            else{
                map["email"] = binding?.etEmailMobile?.text?.trim().toString()
            }

            map["type"] = type
            map["lang"] = SharedPreference.get(requireActivity()).language
            map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
            map["device_type"] = Constants.DEVICE_TYPE
            viewModel.loginWithOtpAffiliateUser(map)
        }
        else{
            if (type==0){
                map["country_code"] = binding?.tvCountryPicker?.text?.trim().toString()
            }
            map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
            map["type"] = type
            map["lang"] = SharedPreference.get(requireActivity()).language
            map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
            map["device_type"] = Constants.DEVICE_TYPE
            viewModel.loginViaEmailOrMobile(map)
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

        viewModel.loginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    clearInputField()
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    findNavController().navigate(R.id.action_loginFragment_to_BannerFragment)
                }
                isObserver = false
            }
        }

        viewModel.loginAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    when(response.data.adminVerify){
                        "0"->{
                            SharedPreference.get(requireActivity()).affiliateAccessToken = response.data.accessToken
                            when{
                                response.data.addressUpdateStatus=="0" ->{
                                    clearInputField()
                                    findNavController().navigate(R.id.action_loginFragment_to_affiliateAddAddressFragment)
                                }
                                response.data.bankUpdateStatus=="0" ->{
                                    clearInputField()
                                    findNavController().navigate(R.id.action_loginFragment_to_affiliateAddBankDetailFragment)
                                }
                            }
                        }
                        "1"->{
                            SharedPreference.get(requireActivity()).affiliateAccessToken = ""
                                CommonMethod.showSuccessWithTimeDialog(requireActivity(),getString(R.string.your_profile_is_under_review), time = 2000){
                            }
                        }
                        "2"->{
                            SharedPreference.get(requireActivity()).affiliateAccessToken = response.data.accessToken
                            SharedPreference.get(requireActivity()).isAffiliateTutorial = true
                            showToast(response.message)
                            clearInputField()
                            requireActivity().startActivity(
                                Intent(requireActivity(),
                                    AffiliateHomeActivity::class.java)
                            )
                            requireActivity().finish()
                        }
                        "3"->{
                            SharedPreference.get(requireActivity()).affiliateAccessToken = ""
                            clearInputField()
                            CommonMethod.showSuccessWithTimeDialog(requireActivity(),"${getString(R.string.your_profile_is_rejected_by_admin)} (${response.data.rejectReason}).", time = 3000){

                            }
                        }
                        "4"->{
                            SharedPreference.get(requireActivity()).affiliateAccessToken = response.data.accessToken
                            CommonMethod.showSuccessWithTimeDialog(requireActivity(),"${getString(R.string.document_rejected_please_reupload_it)} (${response.data.rejectReason})", time = 3000){
                                val bundle = Bundle().apply {
                                    putString("signUpData", Gson().toJson(response.data))
                                }
                                findNavController().navigate(R.id.action_loginFragment_to_affiliateSignUpFragment,bundle)
                                clearInputField()
                            }
                        }
                    }

                }
                isObserver = false
            }
        }

        viewModel.loginEmailMobileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    val bundle = Bundle().apply {
                        if (type==0){
                            putString("mobileNumber", binding?.etEmailMobile?.text.toString().trim())
                            putString("countryCode", binding?.tvCountryPicker?.text.toString().trim())
                        }
                        else{
                            putString("email", binding?.etEmailMobile?.text.toString().trim())
                        }
                        putString("from", "login")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_OtpFragment,bundle)
                    clearInputField()
                }
                isObserver = false
            }
        }

        viewModel.socialLoginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    clearInputField()
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    findNavController().navigate(R.id.action_loginFragment_to_BannerFragment)
                }
                isObserver = false
            }
        }

        viewModel.loginWithOtpAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    val bundle = Bundle().apply {
                        if (type==0){
                            putString("mobileNumber", binding?.etEmailMobile?.text.toString().trim())
                            putString("countryCode", binding?.tvCountryPicker?.text.toString().trim())
                        }
                        else{
                            putString("email", binding?.etEmailMobile?.text.toString().trim())
                        }
                        putString("from", "affiliateLogin")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_OtpFragment,bundle)
                }

                }
                isObserver = false
            }

        viewModel.countryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveCountryList(response.data)
                    setCountry()
                }
                isObserver = false
            }
        }
        viewModel.getSelectedCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveSelectedCountryList(response.data)
                    setSelectedCountry()
                }
                isObserver = false
            }
        }
    }

    private fun validation():Boolean{

        if (binding?.etEmailMobile?.text?.isEmpty() == true) {
            showToast(getString(R.string.please_enter_mobile_number_or_email_address))
            return false
        }
        else if (binding?.etEmailMobile?.text?.length!! < 7&&type==0) {
            showToast(getString(R.string.number_must_be_at_least_7_digits_long))
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmailMobile?.text.toString()).matches() && type==1) {
            showToast(getString(R.string.please_enter_valid_email))
            return false
        }
        else if (binding?.etPassword?.text?.isEmpty() == true&&binding?.btnLoginViaOtp?.text != getString(R.string.login_via_password)) {
            showToast(getString(R.string.please_enter_password))
            return false
        }
        return true
    }

    private fun getCountry() {
        viewModel.getCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setCountry() {
       val data = SharedPreference.get(requireActivity()).getCountryList()
        if (!data.isNullOrEmpty()) {
            countryList.clear()
            data?.let { countryList.addAll(it) }
        }
    }

    private fun setSelectedCountry() {
        val data = SharedPreference.get(requireActivity()).getSelectedCountryList()
        if (!data.isNullOrEmpty()) {
            countryList.clear()
            data?.let { countryList.addAll(it) }
        }
    }

    //Google login
    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->

                if (task.isSuccessful) {

                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: ""
                    val name = user?.displayName ?: ""
                    val email = user?.email ?: ""
                    val profileImage = user?.photoUrl.toString()

                    Log.e("GOOGLE_USER", "Name : $name")
                    Log.e("GOOGLE_USER", "Email : $email")

                    socialLogin(uid, name, email, profileImage)

                } else {
                    val errorMsg = task.exception?.localizedMessage ?: getString(R.string.google_login_failed)
                    Log.e("GOOGLE_SIGN_IN", "Firebase auth failed: ${task.exception?.message}", task.exception)
                    showToast(errorMsg)
                }
            }
    }

    //social login api
    private fun socialLogin(
        uid: String,
        name: String,
        email: String,
        image: String,
        socialType: String = "1"
    ) {

        val map = HashMap<String, Any>()

        map["full_name"] = name
        map["email"] = email
        map["profile_image"] = image
        map["unique_id"] = uid
        map["socialType"] = socialType
        map["device_type"] = Constants.DEVICE_TYPE
        map["device_token"] = SharedPreference.get(requireActivity()).deviceToken
        map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
        map["lang"] = SharedPreference.get(requireActivity()).language
        viewModel.socialLogin(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun printHashKey() {
        try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES
            }
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().packageManager.getPackageInfo(
                    requireActivity().packageName,
                    PackageManager.PackageInfoFlags.of(flags.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                requireActivity().packageManager.getPackageInfo(
                    requireActivity().packageName,
                    flags
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            }

            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.e("HASH_KEY", hashKey)
            }

        } catch (e: Exception) {
            Log.e("HASH_KEY", "Error printing hash key", e)
        }
    }

    private fun printSha1Fingerprint() {
        try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES
            }
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().packageManager.getPackageInfo(
                    requireActivity().packageName,
                    PackageManager.PackageInfoFlags.of(flags.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                requireActivity().packageManager.getPackageInfo(
                    requireActivity().packageName,
                    flags
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            }

            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA-1")
                val digest = md.digest(signature.toByteArray())
                val hexString = digest.joinToString(":") { String.format("%02X", it) }
                Log.d("GOOGLE_SIGN_IN", "Current App SHA-1 Fingerprint: $hexString")
            }
        } catch (e: Exception) {
            Log.e("GOOGLE_SIGN_IN", "Error printing SHA-1 fingerprint", e)
        }
    }

    private fun getFacebookUserData() {

        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { obj, _ ->

            try {

                val id = obj?.getString("id") ?: ""
                val name = obj?.getString("name") ?: ""
                val email = obj?.getString("email") ?: ""

                val image =
                    "https://graph.facebook.com/$id/picture?type=large"

                Log.e("NAME", name)
                Log.e("EMAIL", email)
                Log.e("IMAGE", image)

                if (id.isNotEmpty()) {
                    socialLogin(id, name, email, image, socialType = "2")
                } else {
                    showToast("Facebook login failed: Unable to retrieve user details")
                }

            } catch (e: Exception) {
                Log.e("FACEBOOK", "Error fetching user data", e)
                showToast("Facebook login error: ${e.localizedMessage}")
            }
        }

        val parameters = Bundle()

        parameters.putString(
            "fields",
            "id,name,email,picture.type(large)"
        )

        request.parameters = parameters
        request.executeAsync()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

}
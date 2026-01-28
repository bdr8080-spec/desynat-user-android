package com.desynatuser.ui.home.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProfileDetailBinding
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileDetailFragment : BaseFragment() {
    private var binding: FragmentProfileDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var user: LoginResponse.Data?=null
    var clickType = -1
    var isImageObserver = false
    private lateinit var imagePicker: ImagePicker
    var email = ""
    var mobileNumber = ""
    var countryCode = ""
    private var imageUri: Uri?=null
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
        binding = FragmentProfileDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        user = SharedPreference.get(requireActivity()).getUser()
        email = user?.email.toString()
        mobileNumber = user?.mobile.toString()
        countryCode = user?.countryCode.toString()
        binding?.apply {


            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    profileImage = CommonMethod.uriToFile(requireActivity(), it)
                    imageUri = it
                    ivProfileImage.setImageURI(it)
                    uploadFile(profileImage!!)
                }
            }
        }
    }

    override fun initControl() {

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener{
                findNavController().popBackStack()
               user?.email = email
                user?.mobile = mobileNumber
                user?.countryCode = countryCode

                user?.let { it1 -> SharedPreference.get(requireActivity()).saveUser(it1) }

            }

            tvChangeImage.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            etMobile.setOnClickListener {
                val map = HashMap<String,Any>()
                map["for"] = "mobile" //mobile,email
                map["country_code"] = user?.countryCode.toString()
                map["mobile"] = user?.mobile.toString()
                map["type"] = 0 //0-old,1-new
                clickType = 0
                changeMobileOrEmail(map)
            }

            etEmail.setOnClickListener {
                val map = HashMap<String,Any>()
                map["for"] = "email" //mobile,email
                map["email"] = binding?.etEmail?.text?.trim().toString()
                map["type"] = 0 //0-old,1-new
                clickType = 1
                changeMobileOrEmail(map)
            }

            tvChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_profileDetailsFragment_to_changePasswordFragment)
            }

            btnSaveChange.setOnClickListener {
                    val map = HashMap<String,String>()
                    map["full_name"] = binding?.etFullName?.text.toString().trim()
                    map["image"] = profileImageUrl.toString()
                    map["country_code"] = user?.countryCode.toString()
                    map["mobile"] = user?.mobile.toString()
                    map["email"] = binding?.etEmail?.text.toString().trim()

                    viewModel.updateProfile(map)
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    isObserver = true
            }
        }

    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun changeMobileOrEmail(map:HashMap<String,Any>){
        viewModel.changeNumber(map)
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

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    profileImageUrl = response.data.url

                }
                isImageObserver = false
            }
        }

        viewModel.changeNumberResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    if (clickType==0){
                        val bundle = Bundle().apply {
                            putString("mobileNumber", user?.mobile)
                            putString("countryCode", user?.countryCode)
                            putString("from", "edit")
                            putString("userId", response.data.id)
                        }
                        findNavController().navigate(R.id.action_profileDetailsFragment_to_otpFragment,bundle)

                    }
                    else if (clickType==1){
                        val bundle = Bundle().apply {
                            putString("email", user?.email)
                            putString("from", "edit")
                            putString("userId",response.data.id)
                        }
                        findNavController().navigate(R.id.action_profileDetailsFragment_to_otpFragment,bundle)
                    }
                }
                isObserver = false
            }
        }

        viewModel.updateProfiledResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    CommonMethod.showSuccessDialog(requireActivity(),"Profile updated successfully!"){
                        findNavController().popBackStack()
                    }
                }
                isObserver = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        user = SharedPreference.get(requireActivity()).getUser()
        binding?.apply {
            toolbar.tvToolbar.text = "Profile Details"
            etFullName.setText(user?.fullName)
            etEmail.setText(user?.email)
            etMobile.setText(user?.countryCode+" | "+user?.mobile)
            if (imageUri==null) {
                profileImageUrl = user?.profileImage
                Glide.with(requireActivity()).load(user?.profileImage).placeholder(R.drawable.ic_user).into(ivProfileImage)
            }
        }
    }

}
package com.desynatuser.ui.affiliateUser.home.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.adapter.BankAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateProfileDetailBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileRequest
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.text.toDoubleOrNull

@AndroidEntryPoint
class AffiliateProfileDetailFragment : BaseFragment() {
    private var binding: FragmentAffiliateProfileDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var isProfileObserver = false
    private var isImageObserver = false
    private var profileData : GetAffiliateUserProfileResponse.Data?=null
    private var bankList = ArrayList<GetAffiliateUserProfileResponse.Data.BankDetails>()
    private var bankAdapter: BankAdapter?=null
    private var tabType = 0
    private var updatedType = 0
    private var selectedPos = -1
    private var selectedCountryId = ""
    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private var address:String=""
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private lateinit var imagePicker: ImagePicker
    private var profileImageUrl:String?=null
    private var docUrl1:String?=null
    private var docUrl2:String?=null
    private var imageType = ""
    var clickType = -1

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
        binding = FragmentAffiliateProfileDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.profile_details)

            if (SharedPreference.get(requireActivity()).getSelectedCountryList()==null){
                getSelectedCountry()
            }
            else{
                setCountry()
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    when(imageType){
                        "1"->{
                            ivProfileImage.setImageURI(it)
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

            when(tabType){
                0->{
                    tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                    tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                    tvBank.setTextColor(requireActivity().getColor(R.color.black))
                    tvPersonalInfo.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_left_tab))
                    tvAddress.background = null
                    tvBank.background = null
                    clPersonalInfo.isVisible = true
                    llAddress.isVisible = false
                    llBank.isVisible = false
                    btnSaveChange.isVisible = true
                }
                1->{
                    tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                    tvAddress.setTextColor(requireActivity().getColor(R.color.white))
                    tvBank.setTextColor(requireActivity().getColor(R.color.black))
                    tvAddress.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_mid_tab))
                    tvPersonalInfo.background = null
                    tvBank.background = null
                    clPersonalInfo.isVisible = false
                    llAddress.isVisible = true
                    llBank.isVisible = false
                    btnSaveChange.isVisible = true
                }
                2->{
                    tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                    tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                    tvBank.setTextColor(requireActivity().getColor(R.color.white))
                    tvBank.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_right_tab))
                    tvAddress.background = null
                    tvPersonalInfo.background = null
                    llBank.isVisible = true
                    clPersonalInfo.isVisible = false
                    llAddress.isVisible = false
                    btnSaveChange.isVisible = false
                }
            }

            bankAdapter = BankAdapter(requireActivity(),bankList, isShow = true,object : BankAdapter.OnClick{
                override fun onClick(position: Int,type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("userId", profileData?.userId.toString())
                                putString("from", "profile")
                                putString("type", "edit")
                                putString("bankData", Gson().toJson(bankList[position]))
                            }
                            findNavController().navigate(
                                R.id.action_affiliateProfileDetailFragment_to_affiliateAddBankDetailFragment,bundle
                            )
                        }
                        2->{
                            CommonMethod.deleteAlertDialog(
                                requireActivity(),
                                getString(R.string.are_you_sure_you_want_to_delete_the_bank_detail)
                            ) { action ->
                                when (action) {
                                    CommonMethod.DialogAction.DELETE -> {
                                        selectedPos = position
                                        deleteBank()
                                    }

                                    CommonMethod.DialogAction.DISCARD -> {

                                    }
                                }
                            }
                        }
                        3->{
                            defaultBank(bankList[position].id.toString())
                        }
                    }
                }

            })

            rvBank.adapter = bankAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvPersonalInfo.setOnClickListener {
                tabType = 0
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvPersonalInfo.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_left_tab))
                tvAddress.background = null
                tvBank.background = null
                clPersonalInfo.isVisible = true
                llAddress.isVisible = false
                llBank.isVisible = false
                btnSaveChange.isVisible = true
            }

            tvAddress.setOnClickListener {
                tabType = 1
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_mid_tab))
                tvPersonalInfo.background = null
                tvBank.background = null
                clPersonalInfo.isVisible = false
                llAddress.isVisible = true
                llBank.isVisible = false
                btnSaveChange.isVisible = true
            }

            tvBank.setOnClickListener {
                tabType = 2
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_right_tab))
                tvAddress.background = null
                tvPersonalInfo.background = null
                llBank.isVisible = true
                clPersonalInfo.isVisible = false
                llAddress.isVisible = false
                btnSaveChange.isVisible = false
            }

            tvChangeImage.setOnClickListener {
                imageType = "1"
                imagePicker.showImageSourceDialog()
            }

            tvChangePassword.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from","affiliate")
                }
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_changePasswordFragment,bundle)
            }

            btnSaveChange.setOnClickListener {
                when(tabType){
                    0->{
                        updateAffiliateProfile()
                    }
                    1->{
                        updateAffiliateAddress()
                    }
                }
            }

            tvChangeMobile.setOnClickListener {

                val map = HashMap<String,Any>()
                map["for"] = "mobile" //mobile,email
                map["country_code"] = profileData?.countryCode.toString()
                map["mobile"] = profileData?.mobile.toString()
                map["type"] = 0 //0-old,1-new
                clickType = 0
                changeAffiliateMobileOrEmail(map)
            }

            tvChangeEmail.setOnClickListener {

                val map = HashMap<String,Any>()
                map["for"] = "email" //mobile,email
                map["email"] = binding?.etEmail?.text?.trim().toString()
                map["type"] = 0 //0-old,1-new
                clickType = 1
                changeAffiliateMobileOrEmail(map)
            }

            ivDoc1.setOnClickListener {
                imageType = "2"
                imagePicker.showImageSourceDialog()
            }

            ivDoc2.setOnClickListener {
                imageType = "3"
                imagePicker.showImageSourceDialog()
            }

            etCountry.setOnClickListener {
                CommonMethod.showSelectedCountryPicker(requireContext(),
                    it,
                    countryList,
                    false
                ) { selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            etChooseLocation.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                intent.putExtra("savedLat", latitude)
                intent.putExtra("savedLng", longitude)
                intent.putExtra("savedAddress", address)
                Log.e("send", "initControl: $address $latitude $longitude" )
                locationResultLauncher.launch(intent)
            }

            clAddAnotherAccount.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("userId", profileData?.userId.toString())
                    putString("from", "profile")
                    putString("type", "add")
                }
                findNavController().navigate(
                    R.id.action_affiliateProfileDetailFragment_to_affiliateAddBankDetailFragment,bundle
                )
            }
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

                    imageType = ""
                }
                isImageObserver = false
            }
        }

        viewModel.updateAffiliateUserProfileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    updatedType = 1
                    getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.deleteBankResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    updatedType = 3
                    getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.defaultBankResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    updatedType = 4
                    getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.updateAffiliateUserAddressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    updatedType = 2
                    getAffiliateUserProfile()
                }
                isObserver = false
            }
        }

        viewModel.changeAffiliateNumberResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    if (clickType==0){
                        val bundle = Bundle().apply {
                            putString("mobileNumber", profileData?.mobile)
                            putString("countryCode", profileData?.countryCode)
                            putString("from", "affiliateEdit")
                            putString("userId", response.data.id)
                        }
                        findNavController().navigate(R.id.action_affiliateProfileFragment_to_otpFragment,bundle)

                    }
                    else if (clickType==1){
                        val bundle = Bundle().apply {
                            putString("email", profileData?.email)
                            putString("from", "affiliateEdit")
                            putString("userId",response.data.id)
                        }
                        findNavController().navigate(R.id.action_affiliateProfileFragment_to_otpFragment,bundle)
                    }
                }
                isObserver = false
            }
        }

        viewModel.getAffiliateProfileResult.observe(viewLifecycleOwner) { response ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                    when(updatedType){
                        1->{
                            CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.profile_updated_successfully)){
                                findNavController().popBackStack()
                            }
                        }
                        2->{
                            CommonMethod.showSuccessDialog(requireActivity(),
                                getString(R.string.address_updated_successfully)){
                                findNavController().popBackStack()
                            }
                        }
                        3->{
                            CommonMethod.showSuccessDialog(requireActivity(),
                                getString(R.string.bank_details_deleted_successfully)){
                                profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
                                bankList.clear()
                                profileData?.bankDetails?.let { bankList.addAll(it) }
                                bankAdapter?.notifyDataSetChanged()
                            }
                        }
                        4->{
                            CommonMethod.showSuccessDialog(requireActivity(),
                                getString(R.string.bank_account_changed_successfully)){
                                profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
                                bankList.clear()
                                profileData?.bankDetails?.let { bankList.addAll(it) }
                                bankAdapter?.notifyDataSetChanged()
                            }
                        }
                    }

                }
                updatedType = 0
                isProfileObserver = false
            }
        }
    }
    private fun setData(){
        binding?.apply {
            etFullName.setText(profileData?.fullName)
            etEmail.setText(profileData?.email)
            etMobile.setText(profileData?.countryCode+profileData?.mobile)

            if (profileImageUrl==null){
                profileImageUrl = profileData?.profileImage
                docUrl1 = profileData?.document1
                docUrl2 = profileData?.document2
                 }
            Glide.with(requireActivity()).load(profileImageUrl).placeholder(R.drawable.ic_user).into(ivProfileImage)
            Glide.with(requireActivity()).load(docUrl1).placeholder(R.drawable.doc1).into(ivDoc1)
            Glide.with(requireActivity()).load(docUrl2).placeholder(R.drawable.doc2).into(ivDoc2)

            if (latitude==0.0){
                latitude = profileData?.let?.toDoubleOrNull()?:0.0
                longitude = profileData?.long?.toDoubleOrNull()?:0.0
                selectedCountryId = profileData?.addressDetails?.country.toString()
                for(i in 0 until countryList.size){
                    if (countryList[i].id.toString()==profileData?.addressDetails?.country){
                        etCountry.setText(countryList[i].name)
                    }
                }

                etChooseLocation.setText(profileData?.addressDetails?.locations)
                etAddressLine1.setText(profileData?.addressDetails?.address1)
                etAddressLine2.setText(profileData?.addressDetails?.address2)
                etPinCode.setText(profileData?.addressDetails?.pincode)
            }


            bankList.clear()
            profileData?.bankDetails?.let { bankList.addAll(it) }
            bankAdapter?.notifyDataSetChanged()
        }
    }
    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }
    fun updateAffiliateProfile(){
        binding?.apply {
            val request = UpdateAffiliateProfileRequest(
                email = profileData?.email.toString(),
                mobile = profileData?.mobile.toString(),
                countryCode = profileData?.countryCode.toString(),
                fullName = etFullName.text.toString().trim(),
                profileImage = profileImageUrl.toString(),
                document1 = docUrl1.toString(),
                document2 = docUrl2.toString(),
            )
            viewModel.updateAffiliateUserProfile(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

    fun updateAffiliateAddress(){
        binding?.apply {
            val request = AffiliateUserAddAddressRequest(
                address1 = etAddressLine1.text.toString().trim(),
                address2 = etAddressLine2.text.toString().trim(),
                city = etCity.text.toString(),
             //   country = etCountry.text.toString().trim(),
                country = selectedCountryId,
                let = latitude,
                long = longitude,
                pincode = etPinCode.text.toString().trim(),
                state = etState.text.toString().trim(),
                town = etLocalityTown.text.toString().trim(),
                locations = etChooseLocation.text.toString().trim()
            )
            viewModel.updateAffiliateUserAddress(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

    private fun changeAffiliateMobileOrEmail(map:HashMap<String,Any>){
        viewModel.changeAffiliateNumber(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAffiliateUserProfile(){
        viewModel.getAffiliateProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isProfileObserver = true
    }

    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }
    private fun deleteBank() {
       val map = HashMap<String, Any>()
        map["bank_details_id"] = bankList[selectedPos].id
        viewModel.deleteBank(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

 private fun defaultBank(id: String) {
       val map = HashMap<String, Any>()
        map["id"] = id
        viewModel.defaultBank(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getSelectedCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data
            val city = data?.getStringExtra("city") ?: "N/A"
            val street = data?.getStringExtra("street") ?: "N/A"
            val state = data?.getStringExtra("state") ?: "N/A"
            val pincode = data?.getStringExtra("pincode") ?: "N/A"
            val houseno = data?.getStringExtra("houseno") ?: "N/A"
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            val countryField = data?.getStringExtra("country") ?: "N/A"

            latitude = data?.getDoubleExtra("lat", 0.0)?:0.0
            longitude = data?.getDoubleExtra("lng", 0.0)?:0.0
            address = addressH
            Log.e("send", "getnew: $latitude $longitude" )

            val parsed = parseAddress(addressH)

            val finalHouse = if (houseno.isEmpty()) parsed["house"] else houseno
            val finalStreet = if (street.isEmpty()) parsed["street"] else street
            val finalCity = if (city.isEmpty()) parsed["city"] else city
            val finalState = if (state.isEmpty()) parsed["state"] else state
            val finalPostal = if (pincode.isEmpty()) parsed["postal"] else pincode

            binding?.etChooseLocation?.setText(addressH)
            binding?.etAddressLine1?.setText(addressH)
            binding?.etAddressLine2?.setText(finalHouse + " "+finalStreet)
            binding?.etLocalityTown?.setText(finalCity)
            binding?.etState?.setText(finalState)
            binding?.etPinCode?.setText(finalPostal)
        }
    }

    private fun parseAddress(full: String): Map<String, String> {
        val parts = full.split(",", "-", "،")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var house = ""
        var street = ""
        var city = ""
        var state = ""
        var postal = ""

        parts.forEach { part ->
            when {
                Regex("\\d{5,6}").containsMatchIn(part) -> postal = part
                part.contains("Street", true) || part.contains("Road", true) || part.contains("St", true) ->
                    street = part
                part.contains("Block", true) -> street = part
                city.isEmpty() -> city = part
                state.isEmpty() -> state = part
                house.isEmpty() -> house = part
            }
        }

        return mapOf(
            "house" to house,
            "street" to street,
            "city" to city,
            "state" to state,
            "postal" to postal
        )
    }

    override fun onResume() {
        super.onResume()
        profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
        setData()
    }
}
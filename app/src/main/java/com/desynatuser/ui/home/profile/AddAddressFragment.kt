package com.desynatuser.ui.home.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAddAddressBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.home.profile.response.AddAddressRequest
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : BaseFragment() {
    private var binding: FragmentAddAddressBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var type = -1
    var countryList = ArrayList<GetCountryResponse.Data>()
    private var lat: Double?=0.0
    private var lng: Double?=0.0
    private var addressData: GetAddressResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAddressBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()

        binding?.apply {
            CommonMethod.removeSpace(etName)
            CommonMethod.removeSpace(etAddress)
            CommonMethod.removeSpace(etCity)
            CommonMethod.removeSpace(etBlock)
            CommonMethod.removeSpace(etState)
            CommonMethod.removeSpace(etPostalCode)
            CommonMethod.removeSpace(etHouseNo)
            CommonMethod.removeSpace(etLandmark)
            CommonMethod.removeSpace(etCountry)
        }

        val itemJson = arguments?.getString("addressData")
        if (!itemJson.isNullOrEmpty()) {
            addressData = Gson().fromJson(itemJson, GetAddressResponse.Data::class.java)
            setData()
        }
        else{
            binding?.apply {
                toolbar.tvToolbar.text = "ADD NEW ADDRESS"
            }
        }
        setCountry()


    }

    private fun setData() {
        binding?.apply {
            toolbar.tvToolbar.text = "Edit Address"
            btnAdd.text = "Update Address"

            etName.setText(addressData?.name)
            etAddress.setText(addressData?.address)
            etCountry.setText(addressData?.country)
            etMobile.setText(addressData?.mobile)
            tvCountryPicker.text = addressData?.countryCode
            etHouseNo.setText(addressData?.houseNo)
           // etBlock.setText(addressData?.streetNo)
            etPostalCode.setText(addressData?.postalCode)
            etCity.setText(addressData?.city)
            etState.setText(addressData?.state)
            etLandmark.setText(addressData?.landmark)
            lat = addressData?.latitude?:0.0
            lng = addressData?.longitude?:0.0
            type = addressData?.saveAddressAs?:0

            if (type==0){
                tvOffice.setTextColor(Color.parseColor("#6E4527"))
                tvHome.setTextColor(Color.parseColor("#000000"))
                tvOffice.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_edit_back))
                tvHome.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.edit_back))
                setDrawableTint(tvOffice, "#6E4527")
                setDrawableTint(tvHome, "#000000")
            }
            else{
                tvHome.setTextColor(Color.parseColor("#6E4527"))
                tvOffice.setTextColor(Color.parseColor("#000000"))
                tvHome.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_edit_back))
                tvOffice.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.edit_back))
                setDrawableTint(tvHome, "#6E4527")
                setDrawableTint(tvOffice, "#000000")

            }

        }
    }

    private fun setDrawableTint(textView: AppCompatTextView, color: String) {
        val drawable = textView.compoundDrawables[0]
        drawable?.let {
            val wrapped = DrawableCompat.wrap(it.mutate())
            DrawableCompat.setTint(wrapped, Color.parseColor(color))
            textView.setCompoundDrawablesWithIntrinsicBounds(wrapped, null, null, null)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showUserCountryPicker(requireContext(),
                    clMobile,
                    countryList,true
                ) { selectedCountry ->
                    tvCountryPicker?.text = selectedCountry.dialCode
                }
            }

            etAddress.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)
            }

            tvOffice.setOnClickListener {
                type = 0
                tvOffice.setTextColor(Color.parseColor("#6E4527"))
                tvHome.setTextColor(Color.parseColor("#000000"))
                tvOffice.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_edit_back))
                tvHome.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.edit_back))
                setDrawableTint(tvOffice, "#6E4527")
                setDrawableTint(tvHome, "#000000")
            }

            tvHome.setOnClickListener {
                type = 1
                tvHome.setTextColor(Color.parseColor("#6E4527"))
                tvOffice.setTextColor(Color.parseColor("#000000"))
                tvHome.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_edit_back))
                tvOffice.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.edit_back))
                setDrawableTint(tvHome, "#6E4527")
                setDrawableTint(tvOffice, "#000000")
            }

            btnAdd.setOnClickListener {
                if(validation()){
                   if (addressData!=null){
                       editAddress()
                   }
                    else{
                        addAddress()
                   }
                }
            }
        }
    }

    private fun addAddress(){
        binding?.apply {
            val request = AddAddressRequest(
                address = etAddress.text?.trim().toString(),
                city = etCity.text?.trim().toString(),
                countryCode = tvCountryPicker.text?.trim().toString(),
                houseNo = etHouseNo.text?.trim().toString(),
                landmark = etLandmark.text?.trim().toString(),
                latitude = lat.toString(),
                longitude = lng.toString(),
                mobile = etMobile.text?.trim().toString(),
                name = etName.text?.trim().toString(),
                postalCode = etPostalCode.text?.trim().toString(),
                saveAddressAs = type,
                state = etState.text?.trim().toString(),
           //     streetNo = etBlock.text?.trim().toString(),
                country = etCountry.text?.trim().toString()
            )
            viewModel.addAddress(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

    private fun editAddress(){
        binding?.apply {
            val request = AddAddressRequest(
                address = etAddress.text?.trim().toString(),
                city = etCity.text?.trim().toString(),
                countryCode = tvCountryPicker.text?.trim().toString(),
                houseNo = etHouseNo.text?.trim().toString(),
                landmark = etLandmark.text?.trim().toString(),
                latitude = lat.toString(),
                longitude = lng.toString(),
                mobile = etMobile.text?.trim().toString(),
                name = etName.text?.trim().toString(),
                postalCode = etPostalCode.text?.trim().toString(),
                saveAddressAs = type,
                state = etState.text?.trim().toString(),
              //  streetNo = etBlock.text?.trim().toString(),
                country = etCountry.text?.trim().toString(),
                id = addressData?.id.toString()
            )
            viewModel.editAddress(request)
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

        viewModel.addAddressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Address saved successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.editAddressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Address updated successfully"){
                    findNavController().popBackStack()
                }

                isObserver = false
            }
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

            lat = data?.getDoubleExtra("lat", 0.0)?:0.0
            lng = data?.getDoubleExtra("lng", 0.0)?:0.0

            Log.e("send", "getnew: $lat $lng" )

            val parsed = parseAddress(addressH)

            val finalHouse = if (houseno.isEmpty()) parsed["house"] else houseno
            val finalStreet = if (street.isEmpty()) parsed["street"] else street
            val finalCity = if (city.isEmpty()) parsed["city"] else city
            val finalState = if (state.isEmpty()) parsed["state"] else state
            val finalPostal = if (pincode.isEmpty()) parsed["postal"] else pincode

            binding?.etCountry?.setText(countryField)
            binding?.etAddress?.setText(addressH)
            binding?.etHouseNo?.setText(finalHouse + " "+finalStreet)
          //  binding?.etBlock?.setText(finalStreet)
            binding?.etCity?.setText(finalCity)
            binding?.etState?.setText(finalState)
            binding?.etPostalCode?.setText(finalPostal)
        }
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

    private fun validation(): Boolean {
        binding?.apply {

            val name = etName.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val houseNo = etHouseNo.text.toString().trim()
            val block = etBlock.text.toString().trim()
            val city = etCity.text.toString().trim()
            val state = etState.text.toString().trim()
            val postal = etPostalCode.text.toString().trim()
            val landmark = etLandmark.text.toString().trim()

            if (name.isEmpty()) {
                showToast("Please enter name")
                return false
            }

            if (mobile.isEmpty()) {
                showToast("Please enter mobile number")
                return false
            }

            if (mobile.length < 7) {
                showToast("Please enter a valid mobile number")
                return false
            }

            if (address.isEmpty()) {
                showToast("Please select address")
                return false
            }

            if (houseNo.isEmpty()) {
                showToast("Please enter house/building/area")
                return false
            }

//            if (block.isEmpty()) {
//                showToast("Please enter block/street")
//                return false
//            }

            if (city.isEmpty()) {
                showToast("Please enter city")
                return false
            }

            if (state.isEmpty()) {
                showToast("Please enter state")
                return false
            }

          /*  if (postal.isNotEmpty()) {

                val postalRegex = "^[A-Za-z0-9\\-\\s]{3,10}$".toRegex()

                if (postal.length < 3 || postal.length > 10) {
                    showToast("Postal code must be between 3 to 10 characters")
                    return false
                }

                if (!postalRegex.matches(postal)) {
                    showToast("Please enter a valid postal code")
                    return false
                }
            }*/

            if (type==-1) {
                showToast("Please select address type (Home or Office)")
                return false
            }

            return true
        }
        return false
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

}
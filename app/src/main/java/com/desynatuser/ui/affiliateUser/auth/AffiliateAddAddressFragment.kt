package com.desynatuser.ui.affiliateUser.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateAddAddressBinding
import com.desynatuser.ui.UpdateLocationActivity
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateAddAddressFragment : BaseFragment() {
    private var binding: FragmentAffiliateAddAddressBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private var isObserver = false
    private var userId = ""
    private var selectedCountryId = ""
    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    var address:String=""
    var countryList = ArrayList<GetCountryResponse.Data>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateAddAddressBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        userId = arguments?.getString("userId").toString()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.address)
        }

        if (SharedPreference.get(requireActivity()).getSelectedCountryList()==null){
            getSelectedCountry()
        }
        else{
            setCountry()
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {
                CommonMethod.showSelectedCountryPicker(requireContext(),
                    it,
                    countryList,
                    false
                ) { selectedCountry ->
                    etCountry.setText(if (Constants.language=="ar") selectedCountry?.nameAr ?: selectedCountry?.name else selectedCountry.name)
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

            btnContinue.setOnClickListener {
                if (validation()){
                    addAffiliateUserAddress()
                }
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

        viewModel.addAddressAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.address_save_successfully)
                    ) {
                        val bundle = Bundle().apply {
                            putString("userId", userId)
                            putString("from", "auth")
                            putString("type", "add")
                        }
                        findNavController().navigate(
                            R.id.action_affiliateAddAddressFragment_to_affiliateAddBankDetailFragment,bundle
                        )
                    }
                }
                isObserver = false
            }
        }
    }

    private fun addAffiliateUserAddress(){
        binding?.apply {
            val request = AffiliateUserAddAddressRequest(
                address1 = etAddressLine1.text.toString().trim(),
                address2 = etAddressLine2.text.toString().trim(),
                city = etCity.text.toString(),
                country = etCountry.text.toString().trim(),
                let = latitude,
                long = longitude,
                pincode = etPinCode.text.toString().trim(),
                state = etState.text.toString().trim(),
                town = etLocalityTown.text.toString().trim(),
                locations = etChooseLocation.text.toString().trim()
            )
            viewModel.addAffiliateUserAddress(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            return when {
                etCountry.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_select_country))
                    false
                }
                etChooseLocation.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_select_address))
                    false
                }
                etAddressLine1.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_address_line_1))
                    false
                }
                etAddressLine2.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_address_line_2))
                    false
                }
             /*   etPinCode.text.toString().isEmpty() -> {
                    showToast("Please enter pin code")
                    false
                }
                etLocalityTown.text.toString().isEmpty() -> {
                    showToast("Please enter locality town")
                    false
                }
                etState.text.toString().isEmpty() -> {
                    showToast("Please enter state")
                    false
                }
                etCity.text.toString().isEmpty() -> {
                    showToast("Please enter city")
                    false
                }*/

                else -> true
            }
        }
        return false
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


    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}
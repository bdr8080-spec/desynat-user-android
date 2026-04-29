package com.desynatuser.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.CurrencyAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeCurrencyBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChangeCurrencyFragment : BaseFragment() {
    private var binding: FragmentChangeCurrencyBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var currencyAdapter: CurrencyAdapter?=null
    private var countryList= ArrayList<GetCountryResponse.Data>()
    private var selectedPos = -1
    private var userInfo: LoginResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeCurrencyBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Change Currency"
          userInfo = SharedPreference.get(requireActivity()).getUser()

            currencyAdapter = CurrencyAdapter(requireActivity(),countryList,object :
                CurrencyAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position

                }

            })
            rvCurrency.adapter = currencyAdapter


            if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
                countryList.clear()
                if (SharedPreference.get(requireActivity()).getSelectedCountryList()?.isNotEmpty() == true){
                    val data =  SharedPreference.get(requireActivity()).getSelectedCountryList()
                    countryList.clear()
                    data?.let { countryList.addAll(it) }
                    selectedPos = -1
                    for (i in 0 until countryList.size) {
                        if (countryList[i].id.toString() == userInfo?.selectedCurrency) {
                            selectedPos = i
                            break
                        }
                    }

                    if (selectedPos == -1) {
                        for (i in 0 until countryList.size) {
                            if (countryList[i].currencyCode == "KWD") {
                                selectedPos = i
                                break
                            }
                        }
                    }

                    if (selectedPos == -1 && countryList.isNotEmpty()) {
                        selectedPos = 0
                    }

                    currencyAdapter?.selectedPos = selectedPos
                    currencyAdapter?.notifyDataSetChanged()
                }
                else{
                    getSelectedCountry()
                }
            }
            else{
                countryList.clear()
                if (SharedPreference.get(requireActivity()).getCountryList()?.isNotEmpty() == true){
                    val data =  SharedPreference.get(requireActivity()).getCountryList()
                    countryList.clear()
                    data?.let { countryList.addAll(it) }

                    selectedPos = -1

                    for (i in 0 until countryList.size) {
                        if (countryList[i].id.toString() == userInfo?.selectedCurrency) {
                            selectedPos = i
                            break
                        }
                    }

                    if (selectedPos == -1) {
                        for (i in 0 until countryList.size) {
                            if (countryList[i].currencyCode == "KWD") {
                                selectedPos = i
                                break
                            }
                        }
                    }

                    if (selectedPos == -1 && countryList.isNotEmpty()) {
                        selectedPos = 0
                    }

                    currencyAdapter?.selectedPos = selectedPos
                    currencyAdapter?.notifyDataSetChanged()

                    getCurrencyData(countryList[selectedPos].currencyCode)

                    currencyAdapter?.selectedPos = selectedPos
                    currencyAdapter?.notifyDataSetChanged()
                }

            }

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                changeCurrency()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.getSelectedCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveSelectedCountryList(response.data)
                    countryList.clear()
                    response.data?.let { countryList.addAll(it) }
                    for (i in 0 until countryList.size) {
                        if (countryList[i].id.toString() == userInfo?.selectedCurrency) {
                            selectedPos = i
                            break
                        }
                    }

                    currencyAdapter?.selectedPos = selectedPos
                    currencyAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }

        viewModel.changeCurrencyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                getCurrencyData(countryList[selectedPos].currencyCode)
                userInfo?.selectedCurrency = countryList[selectedPos].id.toString()
                userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.affiliateChangeCurrencyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                getCurrencyData(countryList[selectedPos].currencyCode)
                userInfo?.selectedCurrency = countryList[selectedPos].id.toString()
                userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun changeCurrency(){
        val map = HashMap<String, Any>()
        map["currency"] = countryList[selectedPos].id
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.affiliateChangeCurrency(map)
        }
        else{
            viewModel.changeCurrency(map)
        }
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun getCurrencyData(code: String) {
        Constants.CURRENCY = if (code == "KWD") "KD" else code
    }

}
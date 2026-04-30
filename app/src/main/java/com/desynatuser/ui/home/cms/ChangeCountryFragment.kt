package com.desynatuser.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.ChangeCountryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeCountryBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChangeCountryFragment : BaseFragment() {
    private var binding: FragmentChangeCountryBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var countryAdapter: ChangeCountryAdapter?=null
    private var countryList= ArrayList<GetCountryResponse.Data>()
    var selectedPos = -1
    private var userInfo: LoginResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeCountryBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.change_country)
            userInfo = SharedPreference.get(requireActivity()).getUser()

            countryAdapter = ChangeCountryAdapter(requireActivity(),countryList,object :
                ChangeCountryAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position
                }

            })
            rvCountry.adapter = countryAdapter

            val data = if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
                SharedPreference.get(requireActivity()).getSelectedCountryList()
            }
            else{
                SharedPreference.get(requireActivity()).getCountryList()
            }

            countryList.clear()
            data?.let { countryList.addAll(it) }
            for (i in 0 until countryList.size) {
                if (countryList[i].id.toString() == userInfo?.selectedCountry) {
                    selectedPos = i
                    break
                }
            }

            countryAdapter?.selectedPos = selectedPos
            countryAdapter?.notifyDataSetChanged()

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                changeCountry()
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

        viewModel.changeCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                userInfo?.selectedCurrency = countryList[selectedPos].id.toString()
                userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.affiliateChangeCountryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                userInfo?.selectedCurrency = countryList[selectedPos].id.toString()
                userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun changeCountry(){
        val map = HashMap<String, Any>()
        map["country_id"] = countryList[selectedPos].id
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.affiliateChangeCountry(map)
        }
        else{
        viewModel.changeCountry(map)
        }
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }
}
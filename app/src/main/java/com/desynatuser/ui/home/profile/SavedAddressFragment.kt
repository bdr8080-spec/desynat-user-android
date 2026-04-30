package com.desynatuser.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.SavedAddressAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSavedAddressBinding
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedAddressFragment : BaseFragment() {
    private var binding: FragmentSavedAddressBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var savedAddressAdapter: SavedAddressAdapter?=null
    private var savedAddressList = ArrayList<GetAddressResponse.Data>()
    var selectedPos = -1
    var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedAddressBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = getString(R.string.saved_address)

            from = arguments?.getString("from").toString()
            if (from=="myBag"||from=="productDetail"||from=="chat"){
                btnProceed.isVisible = true
            }
            else{
                btnProceed.isVisible = false
            }

            savedAddressList.clear()
            savedAddressAdapter = SavedAddressAdapter(requireActivity(),savedAddressList,object :SavedAddressAdapter.AddressClick{
                override fun onAddressClick(position: Int, type: Int) {

                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("addressData", Gson().toJson(savedAddressList[position]))
                            }
                            findNavController().navigate(R.id.action_savedAddressFragment_to_addAddressFragment,bundle)
                        }
                        2->{
                            CommonMethod.deleteAlertDialog(
                                requireActivity(),
                                getString(R.string.are_you_sure_you_want_to_delete_the_address)
                            ) { action ->
                                when (action) {
                                    CommonMethod.DialogAction.DELETE -> {
                                        selectedPos = position
                                        deleteAddress()
                                    }

                                    CommonMethod.DialogAction.DISCARD -> {

                                    }
                                }
                            }

                        }
                        3->{

//                                selectedPos = position
//                                setDefaultAddress()

                            if (from=="myBag"||from=="productDetail"){
                                selectedPos = position
                                for (i in 0 until savedAddressList.size){
                                    savedAddressList[i].setAsDefault = "0"
                                }
                                savedAddressList[selectedPos].setAsDefault = "1"
                                savedAddressAdapter?.notifyDataSetChanged()
                            }
                            else{
                                selectedPos = position
                                setDefaultAddress()
                            }
                        }
                    }
                }

            })

            rvAddress.adapter = savedAddressAdapter
        }

        getSavedAddress()

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAddNewAddress.setOnClickListener {
                findNavController().navigate(R.id.action_savedAddressFragment_to_addAddressFragment)
            }

            btnProceed.setOnClickListener {
                if (selectedPos!=-1) {
                    setDefaultAddress()
                }
                else{
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun getSavedAddress(){
        viewModel.getAddress()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setDefaultAddress(){
        val map = HashMap<String,Any>()
        map["address_id"]= savedAddressList[selectedPos].id
        viewModel.setDefaultAddress(map)
        isObserver = true
    }

    private fun deleteAddress(){
        viewModel.deleteAddress(savedAddressList[selectedPos].id.toString())
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

        viewModel.addressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                savedAddressList.clear()
                savedAddressList.addAll(response.data)
                checkData()
                savedAddressAdapter?.notifyDataSetChanged()
                if (savedAddressList.isNotEmpty()){
                    val defaultIndex = savedAddressList.indexOfFirst { it.setAsDefault == "1" }

                    if (defaultIndex == -1) {
                        selectedPos = 0
                        setDefaultAddress()
                    }
                    else{
                        SharedPreference.get(requireActivity()).saveDefaultAddress(savedAddressList[defaultIndex])
                        ProgressDialogUtils.getInstance().hideProgress()
                    }
                }
                else{
                    ProgressDialogUtils.getInstance().hideProgress()
                }
                isObserver = false
            }
        }

        viewModel.defaultAddressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                for (i in 0 until savedAddressList.size){
                  savedAddressList[i].setAsDefault = "0"
                }
                savedAddressList[selectedPos].setAsDefault = "1"
                savedAddressAdapter?.notifyDataSetChanged()
                SharedPreference.get(requireActivity()).saveDefaultAddress(savedAddressList[selectedPos])
                if (from=="myBag"||from=="productDetail"||from=="chat"){
                    val bundle = Bundle().apply {
                        putString("address", Gson().toJson(savedAddressList[selectedPos]))
                    }
                    parentFragmentManager.setFragmentResult("address_request", bundle)

                    findNavController().navigateUp()
                }
                isObserver = false
            }
        }

        viewModel.deleteAddressResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                savedAddressList.removeAt(selectedPos)
                savedAddressAdapter?.notifyItemRemoved(selectedPos)
                savedAddressAdapter?.notifyItemRangeChanged(selectedPos, savedAddressList.size - selectedPos)
                checkData()
                selectedPos = -1
                isObserver = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = savedAddressList.isEmpty()
    }

}
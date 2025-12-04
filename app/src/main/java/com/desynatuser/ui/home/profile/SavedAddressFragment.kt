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
import com.desynatuser.ui.home.response.GetAddressResponse
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
        binding?.apply {
            toolbar.tvToolbar.text = "Saved Address"

            from = arguments?.getString("from").toString()

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
                                "Are you sure you want to delete the address?"
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
                          /*  if (from=="foodBag"){
                                val bundle = Bundle().apply {
                                    putString("address", savedAddressList[position].address)
                                }
                                parentFragmentManager.setFragmentResult("address_request", bundle)

                                findNavController().navigateUp()
                            }
                            else{*/
                                selectedPos = position
                                setDefaultAddress()
//                                savedAddressList[position].isSelected = !savedAddressList[position].isSelected
//                                savedAddressAdapter?.notifyDataSetChanged()
                         //   }
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
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
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
                if (from=="foodBag"||from=="productDetail"){
                    val bundle = Bundle().apply {
                        putString("address", savedAddressList[selectedPos].address)
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
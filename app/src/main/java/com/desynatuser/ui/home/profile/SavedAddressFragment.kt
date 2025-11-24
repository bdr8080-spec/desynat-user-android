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
import com.desynatuser.utils.ProgressDialogUtils
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
                ProgressDialogUtils.getInstance().hideProgress()
                savedAddressList.clear()
                savedAddressList.addAll(response.data)
                checkData()
                savedAddressAdapter?.notifyDataSetChanged()
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
                selectedPos = -1
                isObserver = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = savedAddressList.isEmpty()
    }

}
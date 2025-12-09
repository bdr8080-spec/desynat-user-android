package com.desynatuser.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.PromoCodeAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentPromoCodeBinding
import com.desynatuser.ui.home.response.GetPromoCodeResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoCodeFragment : BaseFragment() {
    private var binding: FragmentPromoCodeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var promoCodeAdapter: PromoCodeAdapter?=null
    private var promoCodeList = ArrayList<GetPromoCodeResponse.Data>()
    var deliveryid = ""
    var amount = ""
    var selectedPos = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoCodeBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Promocode"

            amount = arguments?.getString("amount").toString()
            deliveryid = arguments?.getString("deliveryid").toString()

            promoCodeList.clear()
            promoCodeAdapter = PromoCodeAdapter(requireActivity(),promoCodeList,object :PromoCodeAdapter.PromoCodeClick{
                override fun onPromoCodeClick(position: Int) {
                        selectedPos = position
                    applyPromoCode()
                }

            })

            rvPromoCode.adapter = promoCodeAdapter
        }

        getPromoCode()

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnApply.setOnClickListener {
                if (etPromoCode.text?.trim()?.isNotEmpty() == true){
                   for(i in 0 until promoCodeList.size){
                       if (etPromoCode.text?.trim().toString()==promoCodeList[i].nameEnglish) {
                               val bundle = Bundle().apply {
                                   putString("promoCode", Gson().toJson(promoCodeList[i]))
                               }
                               parentFragmentManager.setFragmentResult("promocode_request", bundle)

                               findNavController().navigateUp()
                           }
                   }
                }
                else{
                    showToast("Please enter promo code")
                }
            }

        }
    }

    private fun getPromoCode(){
        val map = HashMap<String,Any>()
        map["delivery_address"] = deliveryid
        viewModel.getPromoCode(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun applyPromoCode(){
        val map = HashMap<String,Any>()
        map["promo_id"] = promoCodeList[selectedPos].id
        map["amount"] = amount
        viewModel.applyPromoCode(map)
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

        viewModel.promoCodeListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                promoCodeList.clear()
                promoCodeList.addAll(response.data)
                checkData()
                promoCodeAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }
        viewModel.applyPromoCodeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                var data = response.data
                data.id = promoCodeList[selectedPos].id.toString()
                showToast(response.message)
                val bundle = Bundle().apply {
                    putString("promoCode", Gson().toJson(data))
                }
                parentFragmentManager.setFragmentResult("promocode_request", bundle)

                findNavController().navigateUp()

                isObserver = false
            }
        }

    }

    private fun checkData() {
        binding?.tvNoDatFound?.isVisible = promoCodeList.isEmpty()
    }


}
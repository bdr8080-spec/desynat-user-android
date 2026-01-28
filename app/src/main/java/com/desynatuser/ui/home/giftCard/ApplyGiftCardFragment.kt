package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentApplyGiftCardBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyGiftCardFragment : BaseFragment() {
    private var binding: FragmentApplyGiftCardBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var from = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyGiftCardBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        from = arguments?.getString("from").toString()
        binding?.apply {
            toolbar.tvToolbar.text = "Gift Card"
            }
        }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnRedeem.setOnClickListener{
                if(validation()){
                    applyGiftCard()
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

        viewModel.applyGiftCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast("Gift card apply successfully")
                val bundle = Bundle().apply {
                    putString("giftCard", Gson().toJson(response.data))
                    putString("giftCardNumber", binding?.etGiftCardNumber?.text?.trim().toString())
                }
                parentFragmentManager.setFragmentResult("gift_card_request", bundle)

                findNavController().navigateUp()
                isObserver = false
            }
        }

        viewModel.redeemGiftCardInWalletResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Gift Card Redeemed successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun validation():Boolean{
        val cardNumber = binding?.etGiftCardNumber?.text.toString().trim()
        val pin = binding?.etPin?.text.toString().trim()

        if (cardNumber.isEmpty()) {
            showToast("Please enter 16 Digit Gift Card Number")
            return false
        }
        if (cardNumber.length<16) {
            showToast("Gift Card Number must be 16 digit")
            return false
        }

        else if (pin.isEmpty() ) {
            showToast("Please enter pin")
            return false
        }
        return true
    }

    fun applyGiftCard(){
        val map = HashMap<String,Any>()
        map["giftCardNo"] = binding?.etGiftCardNumber?.text?.trim().toString()
        map["giftCardPin"] = binding?.etPin?.text?.trim().toString()
        if (from=="wallet"){
            viewModel.redeemGiftCardInWallet(map)
        }
        else{
            viewModel.applyGiftCard(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}
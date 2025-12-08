package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentEnterGiftCardDetailBinding
import com.desynatuser.ui.home.giftCard.response.AddGiftCardRequest
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterGiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentEnterGiftCardDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var occasionData : GetGiftCardOccasionTypeResponse.Data?=null
    var amount = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterGiftCardDetailBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Enter Details"


            val itemJson = arguments?.getString("occasionData")
            if (!itemJson.isNullOrEmpty()) {
                occasionData = Gson().fromJson(itemJson, GetGiftCardOccasionTypeResponse.Data::class.java)
            }
        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tv100.setOnClickListener {
                amount = "100"
                tv100.setTextColor(requireActivity().getColor(R.color.white))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))

                tv100.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                tv200.backgroundTintList = null
                tvEnterAmount.backgroundTintList = null
                etAmount.isVisible = false
            }

            tv200.setOnClickListener {
                amount = "200"
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.white))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))

                tv100.backgroundTintList = null
                tv200.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                tvEnterAmount.backgroundTintList = null

                etAmount.isVisible = false
            }

            tvEnterAmount.setOnClickListener {
                amount = ""
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.white))

                tv100.backgroundTintList = null
                tv200.backgroundTintList = null
                tvEnterAmount.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                etAmount.isVisible = true
            }

            btnPay.setOnClickListener {
                buyGiftCard()
                findNavController().navigate(R.id.action_enterGiftCardDetailFragment_to_paymentFragment)
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

        viewModel.buyGiftCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                findNavController().navigate(R.id.action_occasionTypeFragment_to_enterGiftCardDetailFragment)
                isObserver = false
            }
        }
    }

    private fun buyGiftCard(){
        if (amount==""){
            amount = binding?.etAmount?.text.toString()
        }
        val request = AddGiftCardRequest(
            amount = amount,
            giftCardId = occasionData?.id.toString(),
            notes = binding?.etNote?.text?.trim().toString(),
            receiverEmail = binding?.etEmail?.text?.trim().toString(),
            receiverName = binding?.etName?.text?.trim().toString(),
            senderName = binding?.etFromName?.text?.trim().toString(),
          //  senderEmail = binding?.etEmail?.text?.trim().toString(),
            wishMessage = binding?.etWishMsg?.text?.trim().toString()
        )
        viewModel.buyGiftCard(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun applyGiftCard(){
        val map = HashMap<String,Any>()
        map["giftCardNo"] = ""
        map["giftCardPin"] = ""
    }

}

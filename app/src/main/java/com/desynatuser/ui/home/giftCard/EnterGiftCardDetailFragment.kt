package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.CustomCountryPickerBinding
import com.desynatuser.databinding.FragmentEnterGiftCardDetailBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.home.giftCard.response.AddGiftCardRequest
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterGiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentEnterGiftCardDetailBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var occasionData: GetGiftCardOccasionTypeResponse.Data? = null
    var amount = ""
    var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterGiftCardDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        setCountry()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.enter_details)
            CommonMethod.removeSpace(etName)
            CommonMethod.removeSpace(etNote)
            CommonMethod.removeSpace(etWishMsg)
            CommonMethod.removeSpace(etFromName)
            CommonMethod.removeAllTextSpaces(etEmail)
            val itemJson = arguments?.getString("occasionData")
            if (!itemJson.isNullOrEmpty()) {
                occasionData =
                    Gson().fromJson(itemJson, GetGiftCardOccasionTypeResponse.Data::class.java)
            }

            tv100.text = "100 ${Constants.CURRENCY}"
            tv200.text = "200 ${Constants.CURRENCY}"

            etAmount.addTextChangedListener(object : TextWatcher {
                private var isEditing = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    s?.let {
                        if (it.startsWith("0")) {
                            it.replace(0, 1, "")
                        }
                    }

                    isEditing = false
                }
            })
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

            tv100.setOnClickListener {
                amount = "100"
                tv100.setTextColor(requireActivity().getColor(R.color.white))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))

                tv100.background = requireActivity().getDrawable(R.drawable.button_stroke_gradient)
                tv200.background = requireActivity().getDrawable(R.drawable.button_stroke)
                tvEnterAmount.background = requireActivity().getDrawable(R.drawable.button_stroke)
                etAmount.isVisible = false
            }

            tv200.setOnClickListener {
                amount = "200"
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.white))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))


                tv100.background = requireActivity().getDrawable(R.drawable.button_stroke)
                tv200.background = requireActivity().getDrawable(R.drawable.button_stroke_gradient)
                tvEnterAmount.background = requireActivity().getDrawable(R.drawable.button_stroke)

                etAmount.isVisible = false
            }

            tvEnterAmount.setOnClickListener {
                amount = ""
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.white))


                tv100.background = requireActivity().getDrawable(R.drawable.button_stroke)
                tv200.background = requireActivity().getDrawable(R.drawable.button_stroke)
                tvEnterAmount.background =
                    requireActivity().getDrawable(R.drawable.button_stroke_gradient)
                etAmount.isVisible = true
            }

            btnPay.setOnClickListener {
                if (validation()) {
                    buyGiftCard()
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

        viewModel.buyGiftCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                val bundle = Bundle().apply {
                    putString("from", "gift")
                    putString("giftCardData", Gson().toJson(response.data))
                }
                findNavController().navigate(
                    R.id.action_enterGiftCardDetailFragment_to_paymentFragment,
                    bundle
                )
                isObserver = false
            }
        }
    }

    private fun buyGiftCard() {
        if (amount == "") {
            amount = binding?.etAmount?.text.toString()
        }
        val request = AddGiftCardRequest(
            amount = amount,
            giftCardId = occasionData?.id.toString(),
            notes = binding?.etNote?.text?.trim().toString(),
            receiverEmail = binding?.etEmail?.text?.trim().toString(),
            receiverName = binding?.etName?.text?.trim().toString(),
            senderName = binding?.etFromName?.text?.trim().toString(),
            countryCode = binding?.tvCountryPicker?.text?.trim().toString(),
            mobile = binding?.etMobile?.text?.trim().toString(),
            wishMessage = binding?.etWishMsg?.text?.trim().toString()
        )
        viewModel.buyGiftCard(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun validation(): Boolean {
        binding?.apply {
            if (amount == "") {
                if (etAmount.isVisible) {
                    if (etAmount.text?.trim()?.isEmpty() == true) {
                        showToast(getString(R.string.please_enter_amount))
                        return false
                    }
                } else {
                    showToast(getString(R.string.please_choose_amount))
                    return false
                }
            }

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()

            if (name.isEmpty()) {
                showToast(getString(R.string.please_enter_recipient_name))
                return false
            }

            if (email.isEmpty()) {
                showToast(getString(R.string.please_enter_email_id))
                return false
            }

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            if (!emailRegex.matches(email)) {
                showToast(getString(R.string.please_enter_a_valid_email_id))
                return false
            }

            if (mobile.isEmpty()) {
                showToast(getString(R.string.please_enter_mobile_number))
                return false
            }

            if (mobile.length < 7) {
                showToast(getString(R.string.please_enter_a_valid_mobile_number))
                return false
            }

            val fromName = etFromName.text.toString().trim()
            if (fromName.isEmpty()) {
                showToast(getString(R.string.please_enter_your_name))
                return false
            }

            val wishMsg = etWishMsg.text.toString().trim()
            if (wishMsg.isEmpty()) {
                showToast(getString(R.string.please_enter_wish_message))
                return false
            }
//
//            val note = etNote.text.toString().trim()
//            if (note.isEmpty()) {
//                showToast("Please enter note")
//                return false
//            }
            return true
        }

        return false
    }


    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        data?.let { countryList.addAll(it) }
    }

}

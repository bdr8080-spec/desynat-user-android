package com.desynatuser.ui.home.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAddCardBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.openCustomPreSelectedDatePicker
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.getValue

@AndroidEntryPoint
class AddCardFragment : BaseFragment() {
    private var binding: FragmentAddCardBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCardBinding.inflate(layoutInflater, container, false)
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
                toolbar.tvToolbar.text = getString(R.string.add_card)

            }
        }

        override fun initControl() {
            binding?.apply {
                toolbar.tvToolbar.setOnClickListener {
                    findNavController().popBackStack()
                }

                btnSave.setOnClickListener {
                    if (validation()){
                        addCard()
                    }
                }

                etExpiryDate.setOnClickListener {
                    val initialDate = Calendar.getInstance().timeInMillis
                    openCustomPreSelectedDatePicker(requireActivity(), initialDate = initialDate) { date, timestamp ->
                        etExpiryDate.setText(date)
                    }
                }
            }
        }

        override fun observe() {
            viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(errorMessage)
                    isObserver = false
                }
            }

            viewModel.addCardResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.card_added_successfully)){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }
        }

        private fun addCard(){
            val map = HashMap<String, Any>()
            map["card_holder_name"] = binding?.etCardHolderName?.text.toString()
            map["card_number"] = binding?.etCardNumber?.text.toString()
            map["expiry_date"] = CommonMethod.customizeChangeDateFormat(binding?.etExpiryDate?.text.toString(),"dd/MM/yyyy","MM/yy")
            map["cvv"] = binding?.etCvv?.text.toString()
            map["is_future"] = if (binding?.cbFuture?.isChecked == true) "1" else "0"
            viewModel.addCard(map)
            isObserver = true
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }

    private fun validation(): Boolean {
        binding?.apply {

            val name = etCardHolderName.text.toString().trim()
            val cardNumber = etCardNumber.text.toString().trim()
            val expiryDate = etExpiryDate.text.toString().trim()
            val cvv = etCvv.text.toString().trim()

            if (name.isEmpty()) {
                showToast(getString(R.string.please_enter_card_holder_name))
                return false
            }

            if (cardNumber.isEmpty()) {
                showToast(getString(R.string.please_enter_card_number))
                return false
            }

            if (cardNumber.length < 16) {
                showToast(getString(R.string.please_enter_a_valid_card_number))
                return false
            }

            if (expiryDate.isEmpty()) {
                showToast(getString(R.string.please_select_expiry_date))
                return false
            }

            if (cvv.isEmpty()) {
                showToast(getString(R.string.please_enter_cvv))
                return false
            }
            if (cvv.length != 3 || cvv == "000") {
                showToast(getString(R.string.please_enter_a_valid_cvv))
                return false
            }
        }
        return true
    }

    }
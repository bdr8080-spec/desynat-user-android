package com.desynatuser.ui.home.myWallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAddMoneyInWalletBinding
import com.desynatuser.utils.Constants
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMoneyInWalletFragment : BaseFragment() {
    private var binding: FragmentAddMoneyInWalletBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var amount = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMoneyInWalletBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.add_money)

            tv10.text = "10 ${Constants.CURRENCY}"
            tv100.text = "100 ${Constants.CURRENCY}"
            tv500.text = "500 ${Constants.CURRENCY}"

            etAmount.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                    if (!p0.isNullOrEmpty()) {
                        clearPresetSelection()
                        amount = p0.toString().toDoubleOrNull() ?: 0.0
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tv10.setOnClickListener {
                etAmount.setText("")
                amount = 10.00
                tv10.setTextColor(requireActivity().getColor(R.color.white))
                tv10.setBackgroundResource(R.drawable.btn_gradient)
                tv100.setTextColor(requireActivity().getColor(R.color.black))
                tv100.setBackgroundResource(R.drawable.button_stroke)
                tv500.setTextColor(requireActivity().getColor(R.color.black))
                tv500.setBackgroundResource(R.drawable.button_stroke)
            }

            tv100.setOnClickListener {
                etAmount.setText("")
                amount = 100.00
                tv100.setTextColor(requireActivity().getColor(R.color.white))
                tv100.setBackgroundResource(R.drawable.btn_gradient)
                tv10.setTextColor(requireActivity().getColor(R.color.black))
                tv10.setBackgroundResource(R.drawable.button_stroke)
                tv500.setTextColor(requireActivity().getColor(R.color.black))
                tv500.setBackgroundResource(R.drawable.button_stroke)
            }

            tv500.setOnClickListener {
                etAmount.setText("")
                amount = 500.00
                tv500.setTextColor(requireActivity().getColor(R.color.white))
                tv500.setBackgroundResource(R.drawable.btn_gradient)
                tv100.setTextColor(requireActivity().getColor(R.color.black))
                tv100.setBackgroundResource(R.drawable.button_stroke)
                tv10.setTextColor(requireActivity().getColor(R.color.black))
                tv10.setBackgroundResource(R.drawable.button_stroke)
            }

            btnContinue.setOnClickListener {
                if (amount <= 0) {
                    showToast(getString(R.string.please_enter_amount))
                    return@setOnClickListener
                }

                val bundle = Bundle().apply {
                    putString("from", "wallet")
                    putDouble("amount", amount)
                }
                findNavController().navigate(
                    R.id.action_addMoneyInWalletFragment_to_paymentFragment,
                    bundle
                )
            }
        }
    }

    private fun clearPresetSelection() {
        binding?.apply {
            tv10.setTextColor(requireActivity().getColor(R.color.black))
            tv10.setBackgroundResource(R.drawable.button_stroke)

            tv100.setTextColor(requireActivity().getColor(R.color.black))
            tv100.setBackgroundResource(R.drawable.button_stroke)

            tv500.setTextColor(requireActivity().getColor(R.color.black))
            tv500.setBackgroundResource(R.drawable.button_stroke)
        }
    }

    override fun observe() {
    }

}
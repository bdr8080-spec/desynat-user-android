package com.desynatuser.ui.home.myWallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAddMoneyInWalletBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMoneyInWalletFragment : BaseFragment() {
    private var binding: FragmentAddMoneyInWalletBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

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
            toolbar.tvToolbar.text = "Add Money"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tv10.setOnClickListener {
                tv10.setTextColor(requireActivity().getColor(R.color.white))
                tv10.setBackgroundResource(R.drawable.btn_gradient)
                tv100.setTextColor(requireActivity().getColor(R.color.black))
                tv100.setBackgroundResource(R.drawable.button_stroke)
                tv500.setTextColor(requireActivity().getColor(R.color.black))
                tv500.setBackgroundResource(R.drawable.button_stroke)
            }

            tv100.setOnClickListener {
                tv100.setTextColor(requireActivity().getColor(R.color.white))
                tv100.setBackgroundResource(R.drawable.btn_gradient)
                tv10.setTextColor(requireActivity().getColor(R.color.black))
                tv10.setBackgroundResource(R.drawable.button_stroke)
                tv500.setTextColor(requireActivity().getColor(R.color.black))
                tv500.setBackgroundResource(R.drawable.button_stroke)
            }

            tv500.setOnClickListener {
                tv500.setTextColor(requireActivity().getColor(R.color.white))
                tv500.setBackgroundResource(R.drawable.btn_gradient)
                tv100.setTextColor(requireActivity().getColor(R.color.black))
                tv100.setBackgroundResource(R.drawable.button_stroke)
                tv10.setTextColor(requireActivity().getColor(R.color.black))
                tv10.setBackgroundResource(R.drawable.button_stroke)
            }

            btnContinue.setOnClickListener {

            }
        }
    }

    override fun observe() {
    }

}
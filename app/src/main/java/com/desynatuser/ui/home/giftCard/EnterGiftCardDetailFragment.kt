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
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterGiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentEnterGiftCardDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false

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
        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tv100.setOnClickListener {
                tv100.setTextColor(requireActivity().getColor(R.color.white))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))

                tv100.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                tv200.backgroundTintList = null
                tvEnterAmount.backgroundTintList = null
                etAmount.isVisible = false
            }

            tv200.setOnClickListener {
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.white))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.brown))

                tv100.backgroundTintList = null
                tv200.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                tvEnterAmount.backgroundTintList = null

                etAmount.isVisible = false
            }

            tvEnterAmount.setOnClickListener {
                tv100.setTextColor(requireActivity().getColor(R.color.brown))
                tv200.setTextColor(requireActivity().getColor(R.color.brown))
                tvEnterAmount.setTextColor(requireActivity().getColor(R.color.white))

                tv100.backgroundTintList = null
                tv200.backgroundTintList = null
                tvEnterAmount.backgroundTintList = requireActivity().getColorStateList(R.color.brown)
                etAmount.isVisible = true
            }

            btnPay.setOnClickListener {

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
    }

}

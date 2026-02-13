package com.desynatuser.ui.affiliateUser.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateProfileDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateProfileDetailFragment : BaseFragment() {
    private var binding: FragmentAffiliateProfileDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateProfileDetailBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Profile Details"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvPersonalInfo.setOnClickListener {
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvPersonalInfo.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_left_tab))
                tvAddress.background = null
                tvBank.background = null
                clPersonalInfo.isVisible = true
                llAddress.isVisible = false
                llBank.isVisible = false
                btnSaveChange.isVisible = true
            }

            tvAddress.setOnClickListener {
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_mid_tab))
                tvPersonalInfo.background = null
                tvBank.background = null
                clPersonalInfo.isVisible = false
                llAddress.isVisible = true
                llBank.isVisible = false
                btnSaveChange.isVisible = true
            }


            tvBank.setOnClickListener {
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvAddress.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.selected_right_tab))
                tvAddress.background = null
                tvPersonalInfo.background = null
                llBank.isVisible = true
                clPersonalInfo.isVisible = false
                llAddress.isVisible = false
                btnSaveChange.isVisible = false
            }

            tvChangeImage.setOnClickListener {

            }

            tvChangePassword.setOnClickListener {

            }

            btnSaveChange.setOnClickListener {

            }
        }
    }

    override fun observe() {

    }

}

package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateAddAddressBinding
import com.desynatuser.utils.CommonMethod
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateAddAddressFragment : BaseFragment() {
    private var binding: FragmentAffiliateAddAddressBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateAddAddressBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Address"
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            etCountry.setOnClickListener {

            }

            etChooseLocation.setOnClickListener {

            }
        }

    }

    override fun observe() {
    }

}
package com.desynatuser.ui.affiliateUser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateProfileFragment : BaseFragment() {
    private var binding: FragmentAffiliateProfileBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateProfileBinding.inflate(layoutInflater, container, false)
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

        }
    }

    override fun initControl() {
        binding?.apply {

            ivEdit.setOnClickListener{

            }

            relLogout.setOnClickListener {

            }
        }
    }

    override fun observe() {
    }

}
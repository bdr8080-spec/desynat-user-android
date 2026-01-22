package com.desynatuser.ui.affiliateUser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateHomeFragment : BaseFragment() {
    private var binding: FragmentAffiliateHomeBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
    }

    override fun initControl() {
    }

    override fun observe() {
    }

}
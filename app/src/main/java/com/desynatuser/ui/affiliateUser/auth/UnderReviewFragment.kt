package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentUnderReviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnderReviewFragment : BaseFragment() {
    private var binding: FragmentUnderReviewBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUnderReviewBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            navigateToLogin()
        }
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = ""
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                navigateToLogin()
            }

            btnLogout.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    override fun observe() {
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            R.id.login,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.login, false)
                .build()
        )
    }

}
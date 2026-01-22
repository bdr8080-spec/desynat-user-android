package com.desynatuser.ui.affiliateUser.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = ""
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack(R.id.login, false)
            }

            btnLogout.setOnClickListener {
                findNavController().popBackStack(R.id.login, false)
            }
        }
    }

    override fun observe() {
    }

}
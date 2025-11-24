package com.desynatuser.ui.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.FragmentSearchProductListBinding
import com.desynatuser.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchProductListFragment : BaseFragment() {
    private var binding : FragmentSearchProductListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchProductListBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        binding?.apply {

            ivBack.setOnClickListener {
            findNavController().popBackStack()
            }

            tvFilter.setOnClickListener {

            }

            tvSort.setOnClickListener {

            }
        }
    }

    override fun initControl() {
    }

    override fun observe() {
    }

}
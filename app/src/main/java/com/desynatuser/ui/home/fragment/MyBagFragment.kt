package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatuser.base.BaseFragment
import com.desynatuser.databinding.FragmentMyBagBinding

class MyBagFragment : BaseFragment() {
    private var binding: FragmentMyBagBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBagBinding.inflate(layoutInflater,container,false)
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
package com.desynatuser.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.CurrencyAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeCurrencyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChangeCurrencyFragment : BaseFragment() {
    private var binding: FragmentChangeCurrencyBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var currencyAdapter: CurrencyAdapter?=null
    private var currencyList= ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeCurrencyBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Change Currency"

            currencyList.clear()
            currencyList.addAll(arrayListOf("KWD","BHD","QAR","SAR","AED","OMR","USD"))

            currencyAdapter = CurrencyAdapter(requireActivity(),currencyList,object :
                CurrencyAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvCurrency.adapter = currencyAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {

            }
        }
    }

    override fun observe() {
    }
}
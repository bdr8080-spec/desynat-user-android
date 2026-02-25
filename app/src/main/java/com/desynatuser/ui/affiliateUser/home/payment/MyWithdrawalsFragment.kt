package com.desynatuser.ui.affiliateUser.home.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliateWithdrawalAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentMyWithdrawalsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MyWithdrawalsFragment : BaseFragment() {
    private var binding: FragmentMyWithdrawalsBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var withdrawalList = ArrayList<String>()
    private var withdrawalAdapter: AffiliateWithdrawalAdapter?=null
    private var type = 1
    private var search: Job? = null
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyWithdrawalsBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "My Withdrawals"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver){

                        }
                    }

                }
            })

            withdrawalAdapter = AffiliateWithdrawalAdapter(requireActivity(),withdrawalList,object :
                AffiliateWithdrawalAdapter.OnClick{
                override fun click(position: Int, type: Int) {

                }

            })
            rvWithdrawl.adapter = withdrawalAdapter


            when(type){
                1->{
                   upcoming()
                }
                2->{
                    completed()
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvUpcoming.setOnClickListener {
                upcoming()
            }

            tvCompleted.setOnClickListener {
                completed()
            }
        }
    }

    private fun upcoming(){
        binding?.apply {
            type = 1
            tvUpcoming.background = null
            tvUpcoming.setTextColor(requireActivity().getColor(R.color.black))
            tvCompleted.setTextColor(requireActivity().getColor(R.color.white))
            tvCompleted.setBackgroundResource(R.drawable.selected_right_tab)
        }
    }

    private fun completed(){
        binding?.apply {
            type = 2
            tvUpcoming.background = null
            tvUpcoming.setTextColor(requireActivity().getColor(R.color.black))
            tvCompleted.setTextColor(requireActivity().getColor(R.color.white))
            tvCompleted.setBackgroundResource(R.drawable.selected_right_tab)
        }
    }

    override fun observe() {
    }

}
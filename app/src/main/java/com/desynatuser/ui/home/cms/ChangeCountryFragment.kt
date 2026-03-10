package com.desynatuser.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.ChangeCountryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeCountryBinding
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChangeCountryFragment : BaseFragment() {
    private var binding: FragmentChangeCountryBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var countryAdapter: ChangeCountryAdapter?=null
    private var countryList= ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeCountryBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Change Country"
            val data = if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
                SharedPreference.get(requireActivity()).getSelectedCountryList()
            }
            else{
                SharedPreference.get(requireActivity()).getCountryList()
            }

            countryList.clear()
            data?.let { countryList.addAll(it) }

            countryAdapter = ChangeCountryAdapter(requireActivity(),countryList,object :
                ChangeCountryAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvCountry.adapter = countryAdapter
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
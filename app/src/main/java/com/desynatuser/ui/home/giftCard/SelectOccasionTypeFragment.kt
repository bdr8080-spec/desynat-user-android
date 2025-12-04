package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.OccasionTypeAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSelectOccasionTypeBinding
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectOccasionTypeFragment : BaseFragment() {
    private var binding: FragmentSelectOccasionTypeBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false

    private var occasionTypeAdapter: OccasionTypeAdapter?=null
    private var occasionList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectOccasionTypeBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Select Occasion Type"

            occasionList.clear()
            occasionList.addAll(listOf("Birthday","Anniversary","Diwali","EID","Ramadan","Ceremony","Christmas","New Year","Wedding"))

            occasionTypeAdapter = OccasionTypeAdapter(requireActivity(),occasionList,object :OccasionTypeAdapter.OnClick{
                override fun clickOnItem(position: Int) {

                }

            })
            rvOccasion.adapter = occasionTypeAdapter
        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {

            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }
    }

}
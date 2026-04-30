package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.OccasionTypeAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSelectOccasionTypeBinding
import com.desynatuser.ui.home.giftCard.response.AddGiftCardRequest
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectOccasionTypeFragment : BaseFragment() {
    private var binding: FragmentSelectOccasionTypeBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false

    private var occasionTypeAdapter: OccasionTypeAdapter?=null
    private var occasionList = ArrayList<GetGiftCardOccasionTypeResponse.Data>()
    var selectedPos = -1

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
            toolbar.tvToolbar.text = getString(R.string.select_gift_card_category)
            Log.e("selectedPos", "initView: ${selectedPos}")
            occasionList.clear()
           /* val layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }

            rvOccasion.layoutManager = layoutManager*/
            occasionTypeAdapter = OccasionTypeAdapter(requireActivity(),occasionList,object :OccasionTypeAdapter.OnClick{
                override fun clickOnItem(position: Int) {
                    selectedPos = position
                    ivGift.load(occasionList[position].image){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }

            })

            rvOccasion.adapter = occasionTypeAdapter
        }

        getGiftCardOccasion()

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {
                if (selectedPos!=-1) {
                    val bundle = Bundle().apply {
                        putString("occasionData", Gson().toJson(occasionList[selectedPos]))
                    }
                    findNavController().navigate(R.id.action_occasionTypeFragment_to_enterGiftCardDetailFragment,bundle)
                }
                else{
                    showToast(getString(R.string.please_select_gift_card_category))
                }
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

        viewModel.giftCardOccasionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                occasionList.clear()
                occasionList.addAll(response.data)
                occasionTypeAdapter?.notifyDataSetChanged()

                if (selectedPos!=-1) {
                    occasionTypeAdapter?.updateSelectedPos(selectedPos)
                }
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }
    }

    private fun getGiftCardOccasion(){
        viewModel.getGiftCardOccasion()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}
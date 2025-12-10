package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.GiftCardAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentGiftCardBinding
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GiftCardFragment : BaseFragment() {
    private var binding: FragmentGiftCardBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var giftCardAdapter: GiftCardAdapter?=null
    private var giftCardList = ArrayList<GetAllGiftCardResponse.Data>()
    var from = ""
    private var search: Job? = null
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftCardBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Gift Cards"

            from = arguments?.getString("from").toString()

            giftCardList.clear()
            giftCardAdapter = GiftCardAdapter(requireActivity(),giftCardList,object :
                GiftCardAdapter.GiftCardClick{
                override fun onGiftCardClick(position: Int) {

                    val bundle = Bundle().apply {
                        putString("giftData", Gson().toJson(giftCardList[position]))
                    }
                    findNavController().navigate(R.id.action_giftCardFragment_to_giftCardDetailFragment,bundle)
                }

            })

            rvGiftCard.adapter = giftCardAdapter

            getGiftCard()

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
                        if (!isObserver)
                            getGiftCard()
                    }

                }
            })

        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_giftCardFragment_to_occasionTypeFragment)
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

        viewModel.giftCardListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                giftCardList.clear()
                giftCardList.addAll(response.data)
                checkData()
                giftCardAdapter?.notifyDataSetChanged()
                    ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }
    }

    private fun getGiftCard(){
        val map = HashMap<String,Any>()
        if (lastQuery!=null){
        map["search"] = lastQuery.toString()
        }
        viewModel.getGiftCardList(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = giftCardList.isEmpty()
    }

}
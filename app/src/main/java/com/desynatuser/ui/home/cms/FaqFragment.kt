package com.desynatuser.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import com.desynatuser.adapter.FaqAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentFaqBinding
import com.desynatuser.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue

@AndroidEntryPoint
class FaqFragment : BaseFragment() {
    private var binding: FragmentFaqBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var faqAdapter: FaqAdapter?=null
    private var faqList = ArrayList<GetFaqResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFaqBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "FAQ's"

            faqAdapter = FaqAdapter(requireActivity(),faqList,object : FaqAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvFaq.adapter = faqAdapter
        }

        getFaq()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.faqResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    faqList.clear()
                    faqList.addAll(response.data)
                    faqAdapter?.notifyDataSetChanged()

                }
                isObserver = false
            }
        }
    }

    fun getFaq(){
        val map = HashMap<String, Any>()
        map["type"] = 3
        viewModel.faq(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true
    }
}
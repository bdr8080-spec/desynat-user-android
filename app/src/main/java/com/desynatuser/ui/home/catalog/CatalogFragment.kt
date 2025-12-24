package com.desynatuser.ui.home.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.CatalogAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentCatalogBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : BaseFragment() {
    private var binding: FragmentCatalogBinding?=null
    private var catalogList = ArrayList<GetCategoryResponse.Data>()
    private var catalogAdapter : CatalogAdapter?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCatalogBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {

            catalogList.clear()
            catalogAdapter = CatalogAdapter(requireActivity(),catalogList,object :CatalogAdapter.CatalogClick{
                override fun onCatalogClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("categoryData", Gson().toJson(catalogList[position]))
                    }
                    findNavController().navigate(R.id.action_catalogFragment_to_subCategoryFragment,bundle)
                }


            })

            rvCategory.adapter = catalogAdapter

        }
        getAllCategory()
    }


    private fun getAllCategory(){
        viewModel.getCategory()
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {

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


        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                catalogList.clear()
                catalogList.addAll(response.data)
                checkData()
                catalogAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = catalogList.isEmpty()
    }

}
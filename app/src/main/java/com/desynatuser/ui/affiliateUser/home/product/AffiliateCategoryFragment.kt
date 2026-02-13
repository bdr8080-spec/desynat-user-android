package com.desynatuser.ui.affiliateUser.home.product

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliateCategoryAdapter
import com.desynatuser.adapter.AffiliateSubCategoryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateCategoryBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateCategoryFragment :  BaseFragment() {
    private var binding: FragmentAffiliateCategoryBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var isSubCategoryObserver = false
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var affiliateCategoryAdapter : AffiliateCategoryAdapter?=null
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var affiliateSubCategoryAdapter : AffiliateSubCategoryAdapter?=null
    private var selectedCategory : GetCategoryResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateCategoryBinding.inflate(layoutInflater, container, false)
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

            affiliateCategoryAdapter = AffiliateCategoryAdapter(requireActivity(),categoryList,object:
                AffiliateCategoryAdapter.CategoryClick{
                override fun onCategoryClick(position: Int) {
                    selectedCategory = categoryList[position]
                    getAllSubCategory()
                }

            })
            rvCategory.adapter = affiliateCategoryAdapter

            affiliateSubCategoryAdapter = AffiliateSubCategoryAdapter(requireActivity(),subCategoryList,object:
                AffiliateSubCategoryAdapter.SubCategoryClick{
                override fun onSubCategoryClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("subCategoryData", Gson().toJson(subCategoryList[position]))
                            }
                            findNavController().navigate(R.id.action_affiliateCategoryFragment_to_affiliateAllProductFragment)
                        }
                        2->{
                            if(position==-1) {
                                binding?.tvNoDataFound?.isVisible = true
                            }
                            else{
                                binding?.tvNoDataFound?.isVisible = false
                            }
                        }
                    }
                }
            })
            rvSubCategory.adapter = affiliateSubCategoryAdapter

//            etSearch.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//                override fun afterTextChanged(s: Editable?) {
//                    val query = s.toString().trim()
//                    affiliateSubCategoryAdapter?.filter(query)
//                }
//            })
        }

        getAllCategory()
    }

    override fun initControl() {
        binding?.apply {
            relSearch.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateCategoryFragment_to_searchProductFragment)
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

        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                categoryList.clear()
                categoryList.addAll(response.data)
                affiliateCategoryAdapter?.notifyDataSetChanged()
                selectedCategory = categoryList[0]
                getAllSubCategory()
                isObserver = false
            }
        }
        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
        if (isSubCategoryObserver) {
            ProgressDialogUtils.getInstance().hideProgress()
            subCategoryList.clear()
            subCategoryList.addAll(response.data)
            affiliateSubCategoryAdapter?.setData(subCategoryList)
            isSubCategoryObserver = false
        }
       }
    }

    private fun getAllCategory(){
        viewModel.getCategory()
      //  ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAllSubCategory(loading:Boolean=true,search:String?=null){
        viewModel.getSubCategory(selectedCategory?.id.toString(),search)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isSubCategoryObserver = true
    }

}
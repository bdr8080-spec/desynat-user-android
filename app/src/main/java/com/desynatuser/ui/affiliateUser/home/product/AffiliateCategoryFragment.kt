package com.desynatuser.ui.affiliateUser.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatuser.adapter.AffiliateCategoryAdapter
import com.desynatuser.adapter.AffiliateSubCategoryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateCategoryBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateCategoryFragment :  BaseFragment() {
    private var binding: FragmentAffiliateCategoryBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private var isObserver = false
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var affiliateCategoryAdapter : AffiliateCategoryAdapter?=null
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var affiliateSubCategoryAdapter : AffiliateSubCategoryAdapter?=null

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

                }

            })
            rvCategory.adapter = affiliateCategoryAdapter

            affiliateSubCategoryAdapter = AffiliateSubCategoryAdapter(requireActivity(),subCategoryList,object:
                AffiliateSubCategoryAdapter.SubCategoryClick{
                override fun onSubCategoryClick(position: Int) {

                }

            })
            rvSubCategory.adapter = affiliateSubCategoryAdapter

        }
    }

    override fun initControl() {
        binding?.apply {

        }
    }

    override fun observe() {
    }

}
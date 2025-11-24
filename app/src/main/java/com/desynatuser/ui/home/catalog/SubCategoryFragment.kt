package com.desynatuser.ui.home.catalog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.SubCategoryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSubCategoryBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubCategoryFragment : BaseFragment() {
    private var binding: FragmentSubCategoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var subCategoryAdapter : SubCategoryAdapter?=null
    private var isFromSwipe=false
    private var searchSubCategory: Job? = null
    private var lastQuery: String? = null
    private var categoryData: GetCategoryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("categoryData")
        if (!itemJson.isNullOrEmpty()) {
            categoryData = Gson().fromJson(itemJson, GetCategoryResponse.Data::class.java)
        }
        binding?.apply {
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Sub-Category"

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            subCategoryList.clear()
            subCategoryAdapter = SubCategoryAdapter(requireActivity(),subCategoryList,object :SubCategoryAdapter.SubCategoryClick{
                override fun onSubCategoryClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("subCategoryData", Gson().toJson(subCategoryList[position]))
                    }
                    findNavController().navigate(R.id.action_subCategoryFragment_to_storeFragment,bundle)
                }

            })
            rvSubcategory.adapter = subCategoryAdapter

      /*      etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    searchSubCategory?.cancel()
                    searchSubCategory = lifecycleScope.launch {
                        delay(600)
                        isFromSwipe = false
                        if (!isObserver)
                            getAllSubCategory(search =  query.ifEmpty { null })
                    }

                }
            })*/

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getAllSubCategory(false)
            }

           /* nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getAllSubCategory(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }*/

        }
        getAllSubCategory()
    }

    private fun getAllSubCategory(loading:Boolean=true,search:String?=null){
        viewModel.getSubCategory(categoryData?.id.toString(),search)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdminStory.setOnClickListener {

            }

            etSearch.setOnClickListener {
                findNavController().navigate(R.id.action_subCategoryFragment_to_searchProductFragment)
            }

            ivMic.setOnClickListener {

            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                errorMessage?.let { showToast(it) }
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                subCategoryList.clear()
                subCategoryList.addAll(response.data)
                checkData()
                subCategoryAdapter?.notifyDataSetChanged()
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = subCategoryList.isEmpty()
    }

}